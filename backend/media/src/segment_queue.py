# 외부 패키지
from itertools import starmap
from threading import Lock
from collections import deque
import os

# 내부 패키지
from logger import log
from config import IS_INF, SEGMENT_LIST_SIZE
from config import SEGMENT_FILE_INDEX_START, SEGMENT_FILE_INDEX_END, SEGMENT_FILE_NUMBER_START, SEGMENT_FILE_NUMBER_END


######################  ts 관리 큐  ######################
class SegmentQueue:
  def __init__(self, hls_path, metadata:list):
    self.lock = Lock()
    self.queue = deque()
    self.buffer = deque(maxlen=int(SEGMENT_LIST_SIZE)-1)
    self.metadata = metadata

    self.init_segments_from_directory(hls_path, 0, len(metadata))
    self.last_index = len(metadata)

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

  def init_segments_from_directory(self, hls_path, start_index = 0, last_index = 0):
    list = sorted(os.listdir(hls_path))
    for file_name in list:
      file_index = int(file_name[SEGMENT_FILE_INDEX_START:SEGMENT_FILE_INDEX_END])
      file_number = int(file_name[SEGMENT_FILE_NUMBER_START:SEGMENT_FILE_NUMBER_END])

      index_range = range(start_index, last_index)
      if file_index in index_range:
        self.enqueue(file_index, file_number)

  def get_all_segments(self):
    with self.lock:
      return list(self.queue)

  def get_last(self):
    return self.buffer[-1]

  def get_buffer(self):
    return list(self.buffer)

  def get_next_index(self):
    return self.last_index

  def get_now_playing(self):
    return self.metadata[0]

  def add_metadata(self, file_info):
    self.metadata.append(file_info)

  def add_metadata_all(self, file_info_list):
    for file_info in file_info_list:
      self.add_metadata(file_info)

  def get_metadata_from_index_and_column(self, index, column):
    return self.metadata[index][column]

  def clear(self):
    self.queue.clear()
    self.buffer.clear()
    self.metadata.clear()