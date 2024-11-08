# 외부 패키지
from threading import Lock
from collections import deque
import asyncio
import os

# 내부 패키지
from logger import log
from config import CHUNK_SIZE

"""오디오 파일 청크를 관리하는 큐"""
class FileQueue:
  def __init__(self, playlist_path):
    self.playlist_path = playlist_path
    self.queue = deque()
    self.lock = Lock()
    self.initialize_queue()

  """ empty """
  def empty(self):
    return len(self.queue) == 0

  """ peek """
  def my_peek(self, index):
    if 0 <= index < len(self.queue):
      return self.queue[index]
    return None

  """ 파일을 큐에 삽입합니다. """
  def enqueue(self, chunk):
    with self.lock:
      self.queue.append(chunk)

  """ 큐에서 파일을 추출합니다. """
  def dequeue(self):
    if len(self.queue) > 0:
      return self.queue.popleft()
    return None

  """ 채널 시작 시 큐 초기화 및 MP3 파일 로드 """
  def initialize_queue(self):
    mp3_files = sorted(
      [os.path.join(self.playlist_path, f) for f in os.listdir(self.playlist_path) if f.endswith(".mp3")],
      key=lambda x: os.path.basename(x)
    )
    for mp3_file in mp3_files:
      self.enqueue(mp3_file)

  """ 가장 첫 번째 파일의 크기를 반환합니다. """
  def get_first_file_size(self):
    if not self.empty():
      first_file = self.my_peek(0)
      if first_file and os.path.exists(first_file):
        return os.path.getsize(first_file)
    return None
