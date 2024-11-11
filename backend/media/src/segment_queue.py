# 외부 패키지
from itertools import starmap
from threading import Lock
from collections import deque
import os

# 내부 패키지
from logger import log
from config import IS_INF, SEGMENT_LIST_SIZE

######################  ts 관리 큐  ######################
class SegmentQueue:
  def __init__(self, hls_path, last_index):
    self.queue = deque()
    self.lock = Lock()
    self.buffer = deque(maxlen=int(SEGMENT_LIST_SIZE)-1)
    self.init_segments_from_directory(hls_path)
    self.last_index = last_index

  def enqueue(self, index, number):
    with self.lock:
      self.queue.append((index, number))

  def dequeue(self, count=1):
    with self.lock:
      segments = []
      for _ in range(min(count, len(self.queue))):
        segments.append(self.queue.popleft())
      if IS_INF:
        for index, number in segments:
          self.queue.append((index, number))

    self.buffer.extend(starmap(lambda index,number: (index,number), segments))
    return segments

  def init_segments_from_directory(self, hls_path, index=-1):
    list = sorted(os.listdir(hls_path))
    for file_name in list:
      index = int(file_name[8:12])
      number = int(file_name[13:18])

      if index == -1 or index == index:
        self.enqueue(index, number)

  def get_all_segments(self):
    with self.lock:
      return list(self.queue)

  def get_last(self):
    return self.buffer[-1]

  def get_buffer(self):
    return list(self.buffer)

  def get_next_index(self):
    return self.last_index

  def clear(self):
    self.queue.clear()