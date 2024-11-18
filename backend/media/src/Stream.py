# 외부 패키지
import threading
import os
import time

from intervaltree import IntervalTree

# 내부 패키지
from config import SEGMENT_LIST_SIZE
from segmenter import write_m3u8, update_m3u8
from file_util import init_directory, create_or_clear_directory
from logger import log
from segment_queue import SegmentQueue
from segmenter import generate_segment_from_files
from firebase_util import notify_stream_start


class Stream:
    def __init__(self, name):
        log.info(f"[{name}] 새로운 채널을 개설합니다.")

        # 디렉토리 관련 변수
        self.stream_path = ''
        self.playlist_path = ''
        self.hls_path = ''

        # 스트림 관련 변수
        self.name = name
        self.queue: SegmentQueue = SegmentQueue(0)
        self.metadata: IntervalTree = IntervalTree()

        # 스레딩, 동시성 제어 관련 변수
        self.lock = threading.Lock()
        self.future = None
        self.add_future = None
        self.stop_event = threading.Event()

    ######################  스트림 실행 전체 동작 정의  ######################
    def start_streaming(self, stream_manager, initial_file_list, fcm):
        try:
            # 디렉토리 셋업
            self.stream_path, self.playlist_path, self.hls_path = init_directory(self.name)

            # 초기 세그먼트 생성 & 큐에 삽입
            self.init_segment_and_queue(initial_file_list)

            # 초기 m3u8 파일 생성
            self.init_m3u8()

            log.info(f"[{self.name}] 스트리밍 시작")
        except Exception as e:
            log.error(f"[{self.name}] 스트리밍 생성 오류 발생")
            stream_manager.remove_stream(self.name)
            return
        notify_stream_start(fcm['token'], fcm['data'])

        while not self.stop_event.is_set():
            try:
                update_m3u8(self, self.stop_event)
                break
            except Exception as e:
                log.error(f"[{self.name}] 스트리밍 오류 발생 - {e}")
                log.error(f"[{self.name}] 5초 후 재시도합니다.")
                time.sleep(5)



    ######################  초기 파일들로 세그먼트 구성 & 큐 저장  ######################
    def init_segment_and_queue(self, initial_file_list):
        self.metadata, next_start = generate_segment_from_files(
            self.hls_path,
            initial_file_list,
            start = 0
        )
        if next_start != 0:
            self.queue = SegmentQueue(next_start)  # 0부터 next_start 범위의 세그먼트 초기화


    ######################  초기 세그먼트들로 m3u8 구성  ######################
    def init_m3u8(self):
        write_m3u8(
            stream    = self,
            segments  = self.queue.dequeue(SEGMENT_LIST_SIZE),
            m3u8_path = os.path.join(self.stream_path, "index.m3u8")
        )


    ######################  음성 추가  ######################
    def add_audio(self, file_info_list):
        if hasattr(self, 'future') and self.future is not None:
            log.info(f"[{self.name}] 기존 작업이 완료될 때까지 대기 중...")
            self.future.result()  # future가 끝날 때까지 기다림
            log.info(f"[{self.name}] 기존 작업 완료.")

        with self.lock:
            log.info(f"[{self.name}] 오디오를 추가합니다")
            start = self.queue.get_next_index()
            new_metadata, next_start = generate_segment_from_files(
                hls_path        = self.hls_path,
                file_info_list  = file_info_list,
                start           = start
            )
            self.queue.set_next_index(next_start)
            self.queue.init_segments_by_range(start, next_start)
            self.metadata = self.metadata | new_metadata
            log.info(f"[{self.name}] 오디오 추가 완료 - '{range(start, next_start)}'")



    ######################################################
    ######################  Getter  ######################
    def get_metadata_by_index(self, index):
        return self.metadata[index]

    def get_metadata_by_index_and_column(self, index, column):
        try:
            # 인덱스가 유효한지 확인
            if index >= len(self.metadata) or index < 0:
                raise IndexError("Invalid index.")

            # 데이터 접근 및 None 체크
            data = next(iter(self.metadata[index])).data
            if data is None:
                raise ValueError("Metadata is None.")

            # 컬럼 값 확인 및 반환
            value = data.get(column)
            if value is None:
                raise KeyError(f"Column '{column}' not found.")

            return value

        except (IndexError, ValueError, KeyError) as e:
            print(f"Error: {e}")
            return None

    def get_queue(self):
        return self.queue


    ######################  자원 할당 해제  ######################
    def stop_streaming_and_remove_stream(self):
        self.remove_stream()
        create_or_clear_directory(self.hls_path)

    def remove_stream(self):
        # 스레드 종료 명령 ON
        self.stop_event.set()

        # 스레드 종료 대기
        if self.future:
            self.future.result()
        if self.add_future:
            self.add_future.result()

        # 기타 자원들 할당 해제
        self.metadata.clear()
        self.queue.clear()
        log.info(f"[{self.name}] 채널 삭제 완료")