from fastapi import FastAPI
from fastapi.responses import StreamingResponse
from watchdog.observers import Observer
from watchdog.events import FileSystemEventHandler
from contextlib import asynccontextmanager
from fastapi.middleware.cors import CORSMiddleware
import os
import asyncio
from queue import Queue
import threading
import time

app = FastAPI()

# CORS 설정
app.add_middleware(
  CORSMiddleware,
  allow_origins=["http://localhost:3000"],  # React 앱의 주소
  allow_credentials=True,
  allow_methods=["*"],
  allow_headers=["*"],
)

# 서버 lifespan 이벤트 핸들러
@asynccontextmanager
async def lifespan(app: FastAPI):
  initialize_queue()  # 서버 시작 시 큐 초기화
  watcher_thread = threading.Thread(target=start_watcher, daemon=True)
  watcher_thread.start()
  yield
  print("Shutting down server...")

app.router.lifespan_context = lifespan


# 설정
MP3_DIRECTORY = "../streaming_channels/channel_1/sources"
EMPTY_MP3 = "../empty.mp3"
queue = Queue()


# 파일 이벤트 핸들러 정의
class MP3FileHandler(FileSystemEventHandler):
  def on_created(self, event):
    if event.src_path.endswith(".mp3"):
      print(f"New MP3 file detected: {event.src_path}")
      enqueue_mp3(event.src_path)

def enqueue_mp3(file_path):
  # 파일을 큐에 삽입
  queue.put(file_path)



# 디렉토리 내의 MP3 파일을 큐에 정렬하여 추가
def initialize_queue():
  print("초기화")
  mp3_files = sorted(
    [os.path.join(MP3_DIRECTORY, f) for f in os.listdir(MP3_DIRECTORY) if f.endswith(".mp3")],
    key=lambda x: os.path.basename(x)
  )
  for mp3_file in mp3_files:
    enqueue_mp3(mp3_file)



# 비동기 스트리밍 제너레이터
async def audio_stream_generator():
  while True:
    # 큐가 비어 있으면 빈 오디오 파일로 계속 스트리밍
    current_file = queue.get() if not queue.empty() else os.path.join(MP3_DIRECTORY, EMPTY_MP3)

    with open(current_file, "rb") as f:
      chunk = f.read(1024)
      while chunk:
        yield chunk
        chunk = f.read(1024)
        await asyncio.sleep(0.01)  # 청크 간 딜레이로 부드러운 스트리밍 제공

    # 파일 스트리밍이 끝난 후 대기하며 큐를 재확인
    await asyncio.sleep(0.5)



# 스트리밍 엔드포인트
@app.get("/stream")
async def stream():
  return StreamingResponse(audio_stream_generator(), media_type="audio/mpeg")



# 파일 시스템 감시 스레드 시작
def start_watcher():
  event_handler = MP3FileHandler()
  observer = Observer()
  observer.schedule(event_handler, MP3_DIRECTORY, recursive=False)
  observer.start()
  print("Watchdog started to monitor MP3 directory for new files.")
  try:
    while True:
      time.sleep(1)
  except KeyboardInterrupt:
    observer.stop()
  observer.join()


