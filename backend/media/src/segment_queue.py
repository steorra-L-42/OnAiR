# 외부 패키지
from threading import Lock
from collections import deque

# 내부 패키지
from logger import log
from config import SEGMENT_LIST_SIZE


######################  ts 관리 큐  ######################
class SegmentQueue:
  def __init__(self, next_start):
    self.lock = Lock()
    self.queue = deque()                                   # 세그먼트 관리 큐(제일 중요)
    self.buffer = deque(maxlen=int(SEGMENT_LIST_SIZE)-1)   # 현재 스트리밍 중인 세그먼트 큐
    self.next_start = next_start                           # 다음 파일 세그먼트의 시작 인덱스

    self.init_segments_by_range(start = 0, end = self.next_start)

  ######################  세그먼트 입력/ 출력  ######################
  def enqueue(self, index):
      self.queue.append(index)

  def dequeue(self, count=1):
    # 지정한 개수만큼 큐에서 추출
    segments = list()
    actual_count = min(count, len(self.queue))
    with self.lock:
      for _ in range(actual_count):
        segments.append(self.queue.popleft())

    self.buffer.extend(index for index in segments)
    return segments

  ######################  세그먼트 기타 메서드  ######################
  ## 세그먼트 초기화
  def init_segments_by_range(self, start = 0, end = 0):
    self.queue.extend(range(start, end))

  ## 모든 세그먼트 조회
  def get_all_segments(self):
    return list(self.queue)

  ## 현재 스트리밍 중인 세그먼트 조회
  def get_buffer_list(self):
    return list(self.buffer)

  ######################  인덱스  ######################
  ## 다음 인덱스 번호 조회
  def get_next_index(self):
    return self.next_start
  
  ## 다음 인덱스 번호 셋팅
  def set_next_index(self, index):
    with self.lock:
      self.next_start = index
  
  ######################  종료  ######################
  # 자원 해제
  def clear(self):
    self.queue.clear()
    self.buffer.clear()