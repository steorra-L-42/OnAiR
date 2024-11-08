
# 외부 패키지
from watchdog.events import FileSystemEventHandler
from watchdog.observers import Observer
import time

# 내부 패키지
from logger import log
from file_queue import FileQueue


# 파일 이벤트 핸들러 정의
class MP3FileHandler(FileSystemEventHandler):
  def __init__(self, queue:FileQueue):
    super().__init__()
    self.queue = queue  # 사용할 큐를 인스턴스 변수로 저장

  def on_created(self, event):
    if event.src_path.endswith(".mp3"):
      log.info(f"New MP3 file detected: [{event.src_path}]")
      self.queue.enqueue_audio_file(event.src_path)


### 파일 시스템 감시 스레드 시작 ###
def start_watcher(channel):
  watch_path = channel["watch_path"]
  stop_event = channel["stop_event"]
  event_handler = MP3FileHandler(channel["chunk_queue"])

  observer = Observer()
  observer.schedule(event_handler, watch_path, recursive=False)
  observer.start()
  log.info(f"폴더 변경 감지 [{watch_path}]")

  while not stop_event.is_set():  # stop_event가 설정될 때까지 실행
    time.sleep(1)
  log.info(f"파일 변경 감지 스레드 종료")
  observer.stop()
  observer.join()