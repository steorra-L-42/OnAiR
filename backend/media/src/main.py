# 외부 패키지
import time

from fastapi import FastAPI
from fastapi.responses import StreamingResponse
from contextlib import asynccontextmanager
from fastapi.middleware.cors import CORSMiddleware
import asyncio
import threading

# 내부 패키지
from config import EMPTY_MP3, BASIC_CHANNEL_NAME, CHUNK_SIZE
from shared_vars import channels, add_channel
from logger import log

from audio_input_handler import start_watcher


app = FastAPI()


""" CORS 설정 """
app.add_middleware(
  CORSMiddleware,
  allow_origins=["http://localhost:3000"],  # React 앱의 주소
  allow_credentials=True,
  allow_methods=["*"],
  allow_headers=["*"],
)


""" 서버 lifespan 이벤트 핸들러 """
@asynccontextmanager
async def lifespan(app: FastAPI):
  global channels
  log.info("서버 초기화 루틴 시작")
  channel = add_channel(BASIC_CHANNEL_NAME)

  watcher_thread = threading.Thread(
      target=start_watcher,
      args=(channel,),
      daemon=True
  )
  watcher_thread.start()
  channel['watcher_thread'] = watcher_thread

  remove_task = asyncio.create_task(channel['chunk_queue'].remove_expired_chunk())
  channel['remove_task'] = remove_task

  yield
  log.info("서버 종료 루틴 시작")
  for key, channel in channels.items():
    channel["stop_event"].set()  # 종료 신호 보내기
    channel["watcher_thread"].join()  # 스레드 종료 대기
    channel['remove_task'].cancel()

  del channels
  log.info("서버를 종료합니다.")


""" 서버 시작 핸들러 등록 """
app.router.lifespan_context = lifespan


""" 스트리밍 엔드포인트 """
@app.get("/stream")
async def stream():
  channel_name = 'channel_1'
  return StreamingResponse(audio_stream_generator(channel_name), media_type="audio/mpeg")


""" 비동기 스트리밍 Generator """
async def audio_stream_generator(channel_name):
  channel = channels[channel_name]
  queue = channel['chunk_queue']

  while True:
    chunk = queue.my_peek()
    if chunk is not None:
      log.info(f"chunk: [{time.time()}]")
      yield chunk
      await asyncio.sleep(0.064)

    else:
      log.info(f"empty: [{time.time()}]")
      with open(EMPTY_MP3, "rb") as f:
        chunk = f.read(CHUNK_SIZE)
        while chunk:
          yield chunk
          chunk = f.read(CHUNK_SIZE)
          await asyncio.sleep(0.01)
        await asyncio.sleep(0.5)