# 외부 패키지
from threading import Lock
from collections import deque
import os
import re

# 내부 패키지
from logger import log

class SegmentQueue:
  def __init__(self, hls_path):
    self.queue = deque()
    self.lock = Lock()
    self.buffer = -1
    self.current_file = None
    self.init_segments_from_directory(hls_path)

  def enqueue(self, index, number):
    with self.lock:
      self.queue.append((index, number))
      log.info(f"Enqueued segment: index={index}, number={number}")

  def dequeue(self, count=1):
    with self.lock:
      segments = []
      for _ in range(min(count, len(self.queue))):
        segments.append(self.queue.popleft())
      if segments:
        self.buffer = segments[-1][0]
      return segments

  def init_segments_from_directory(self, hls_path):
    if not os.path.exists(hls_path):
      log.warning(f"HLS path not found: {hls_path}")
      return

    # Detect segment files with new pattern `segment_XXX.ts`
    segment_files = sorted([f for f in os.listdir(hls_path) if f.startswith('segment_') and f.endswith('.ts')])

    for file_name in segment_files:
      try:
        # Extract numbers from `segment_XXX.ts` pattern
        match = re.match(r'segment_(\d{3})\.ts', file_name)
        if match:
          index = int(match.group(1))
          self.enqueue(index, index)
      except Exception as e:
        log.error(f"Error parsing segment file {file_name}: {str(e)}")

  def get_all_segments(self):
    with self.lock:
      return list(self.queue)

  def get_buffer(self):
    return self.buffer
