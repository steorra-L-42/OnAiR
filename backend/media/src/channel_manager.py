# 외부 패키지
import os
import threading

# 내부 패키지
from logger import log
from config import SEGMENT_LIST_SIZE
from shared_vars import channels

from segment_queue import SegmentQueue
from segmenter import generate_segment_from_files, write_m3u8, update_m3u8
from file_utils import dir_setup, clear_hls_path
from src.shared_vars import channels_lock


######################  채널 추가  ######################
def add_channel(channel_name, file_info_list):
  try:
    channel = init_channel(channel_name,file_info_list)
    with channels_lock:
      channels[channel_name]['channel_thread_stop_event'] = threading.Event()
    update_m3u8(channel = channel, stop_event = channel['channel_thread_stop_event'])
  except Exception as e:
    log.error(f"[{channel['name']}] 스트리밍 오류 발생 - {e}")


######################  채널 초기화  ######################
def init_channel(channel_name, file_info_list):
  # 디렉토리 생성
  channel_path, playlist_path, hls_path = dir_setup(channel_name)
  log.info(f"[{channel_name}] 채널을 추가합니다 - '{channel_path}'")

  # 초기 세그먼트 생성
  metadata, next_start = generate_segment_from_files(
    hls_path,
    file_info_list,
    start= 0
  )
  if next_start == 0:
    log.error(f"[{channel_name}] 초기 제공 음성 파일이 모두 유효하지 않거나 없습니다.")
    log.error(f"[{channel_name}] 채널 생성을 취소합니다.")
    return

  # channels 변수에 추가
  # 세그먼트 큐 생성 및 초기화(기본 세그먼트 삽입)
  channels[channel_name] = {
    'name': channel_name,
    'queue': SegmentQueue(hls_path, metadata, next_start),  # 세그먼트 폴더 경로와 세그먼트 파일들의 메타 데이터 삽입
    'channel_path': channel_path,
    'hls_path': hls_path,
    'playlist_path': playlist_path
  }
  channel = channels[channel_name]

  # .m3u8 생성
  write_m3u8(
    channel   = channel,
    m3u8_path = os.path.join(channel_path, 'index.m3u8'),
    segments  = channel['queue'].dequeue(SEGMENT_LIST_SIZE)
  )
  log.info(f"[{channel_name}] 스트리밍 시작")
  return channel


######################  채널 삭제  ######################
def remove_channel(channel):
  channel_name = channel['name']
  log.info(f"[{channel_name}] 채널 종료 루틴 시작")

  # m3u8 update task 종료
  channel['channel_thread_stop_event'].set()
  channel['channel_thread'].result()
  log.info(f"[{channel_name}] thread 종료")

  # 자원 할당 해제
  channel['queue'].clear()
  clear_hls_path(channel)
  log.info(f"[{channel_name}] 자원 해제")
  log.info(f"[{channel_name}] 채널 종료")


######################  음악 추가  ######################
def add_audio(channel_name, file_info_list):
  channel = channels[channel_name]
  start = channel['queue'].get_next_index()

  new_metadata, next_start = generate_segment_from_files(
    hls_path       = channel['hls_path'],
    file_info_list = file_info_list,
    start          = start
  )
  
  log.info("락 가지기 전")
  with channel['queue'].lock:
    log.info("락 가지고 있는 중")
    channel['queue'].next_start = next_start
    channel['queue'].metadata = channel['queue'].metadata | new_metadata

  # queue에 세그먼트 파일 저장
  channel['queue'].init_segments_by_range(start, next_start)
  log.info(f"[{channel_name}] 오디오 추가 완료")
