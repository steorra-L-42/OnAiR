# 외부 패키지
import asyncio
import os
import queue

# 내부 패키지
from logger import log
from config import CHUNK_SIZE

"""오디오 파일 청크를 관리하는 큐"""
class ChunkQueue(queue.Queue):
  def __init__(self, playlist_path):
    super().__init__()  # Queue의 초기화 메서드를 호출
    self.playlist_path = playlist_path
    self.initialize_queue()

  """ peek """
  def my_peek(self):
    data = None
    with self.mutex:
      log.info("peek IN")
      if not self.empty():
        data = self.queue[0]
    log.info("peek OUT")
    return data

  """ 청크를 큐에 삽입합니다. """
  def enqueue(self, chunk):
    self.put(chunk)  # Queue의 put 메서드 사용

  """ 큐에서 청크를 추출합니다. """
  def dequeue(self):
    return self.get()  # Queue의 get 메서드 사용

  """ 파일을 청크 단위로 나누어 큐에 삽입합니다. """
  def enqueue_audio_file(self, file_path):
    with open(file_path, "rb") as f:
      chunk = f.read(CHUNK_SIZE)
      while chunk:
        self.enqueue(chunk)
        chunk = f.read(CHUNK_SIZE)

  """ 채널 시작 시 큐 초기화 및 MP3 파일 로드. """
  def initialize_queue(self):
    mp3_files = sorted(
        [os.path.join(self.playlist_path, f) for f in os.listdir(self.playlist_path) if f.endswith(".mp3")],
        key=lambda x: os.path.basename(x)
    )
    for mp3_file in mp3_files:
      self.enqueue_audio_file(mp3_file)

  """ 오래된 청크를 삭제합니다 """
  async def remove_expired_chunk(self):
    log.info("청크 삭제 프로세스 가동")
    try:
      while True:
        await asyncio.sleep(3.008)
        for i in (range(47) if len(self.queue) > 47 else range(len(self.queue))):
          self.get()
    except asyncio.CancelledError:
      log.info(f"청크 삭제 작업 종료")


