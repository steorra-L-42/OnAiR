# 외부 패키지
from itertools import starmap
from threading import Lock
from collections import deque
import os

from intervaltree import IntervalTree

# 내부 패키지
from logger import log
from config import SEGMENT_LIST_SIZE


######################  ts 관리 큐  ######################
class SegmentQueue:
  def __init__(self, hls_path, metadata:IntervalTree, next_start):
    self.lock = Lock()
    self.queue = deque()    # 세그먼트 관리 큐(제일 중요)
    self.buffer = deque(maxlen=int(SEGMENT_LIST_SIZE)-1)  # 현재 스트리밍 중인 세그먼트 큐
    self.metadata:IntervalTree = metadata    # 세그먼트 파일들의 메타 데이터
    self.next_start = next_start   # 다음 파일 세그먼트의 시작 인덱스

    self.init_segments_by_range(start = 0, end = self.next_start)

  def enqueue(self, index, number):
    with self.lock:
      self.queue.append((index, number))

  def dequeue(self, count=1):
    # 지정한 개수만큼 큐에서 추출
    segments = list()
    actual_count = min(count, len(self.queue))
    with self.lock:
      for _ in range(actual_count):
        segments.append(self.queue.popleft())

    self.buffer.extend(index for index in segments)
    return segments

  ## 파일의 인덱스 범위가 주어질 때 해당되는 파일 세그먼트들을 큐에 삽입
  def init_segments_by_range(self, start = 0, end = 0):
    self.queue.extend(range(start, end))

  ## 큐의 모든 세그먼트 조회
  def get_all_segments(self):
    with self.lock:
      return list(self.queue)

  # 큐의 현재 스트리밍 중인 세그먼트 조회
  def get_buffer_list(self):
    return list(self.buffer)

  # 메타데이터 조회(임시)
  def get_metadata_from_index(self, index, column):
    data = next(iter(self.metadata[index])).data
    return data[column]

  # 다음 인덱스 번호
  def get_next_index(self):
    return self.next_start

  # 자원 해제
  def clear(self):
    self.queue.clear()
    self.buffer.clear()
    self.metadata.clear()