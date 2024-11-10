# 외부 패키지
from threading import Lock
from collections import deque
import os

# 내부 패키지
from logger import log

######################  ts 관리 큐  ######################
class SegmentQueue:
  def __init__(self, hls_path, last_index):
    self.queue = deque()
    self.lock = Lock()

    self._now_index = 0
    self._now_number = 0
    self.init_segments_from_directory(hls_path)

  def enqueue(self, index, number):
    with self.lock:
      self.queue.append((index, number))

  def dequeue(self, count=1):
    with self.lock:
      segments = []
      for _ in range(min(count, len(self.queue))):
        segments.append(self.queue.popleft())
    return segments

  def init_segments_from_directory(self, hls_path):
    for file_name in os.listdir(hls_path):
      index = int(file_name[8:12])
      number = int(file_name[13:18])
      self.enqueue(index, number)

  def get_all_segments(self):
    with self.lock:
      return list(self.queue)