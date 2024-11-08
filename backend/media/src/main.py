# 외부 패키지
import time

from fastapi import FastAPI, Request
from fastapi.responses import StreamingResponse
from contextlib import asynccontextmanager
from fastapi.middleware.cors import CORSMiddleware
import asyncio
import aiofiles
import threading

# 내부 패키지
from config import EMPTY_MP3, BASIC_CHANNEL_NAME, CHUNK_SIZE
from shared_vars import channels, add_channel
from logger import log

from audio_input_handler import start_watcher


app = FastAPI()


######################  CORS 설정  ######################
app.add_middleware(
  CORSMiddleware,
  allow_origins=["http://localhost:3000"],  # React 앱의 주소
  allow_credentials=True,
  allow_methods=["*"],
  allow_headers=["*"],
)



#####################  서버 lifespan 이벤트 핸들러  #####################
@asynccontextmanager
async def lifespan(app: FastAPI):
  global channels
  log.info("서버 초기화 루틴 시작")
  channel = add_channel(BASIC_CHANNEL_NAME)


  yield
  log.info("서버 종료 루틴 시작")

"""  서버 시작 핸들러 등록 """
app.router.lifespan_context = lifespan




########################  스트리밍 엔드포인트  ########################
@app.get("/stream")
async def stream(request: Request):
  channel_name = 'channel_1'
  file_size = channels[channel_name]['file_size']
  start, end  = None, None

  range_header = request.headers.get('Range')
  if range_header:
    range_type, range_values = range_header.split('=')
    if range_type.strip() == 'bytes':
      range_values = range_values.split('-')
      start = int(range_values[0]) if range_values[0] else None
      end = int(range_values[1]) if len(range_values) > 1 and range_values[1] else None

  start = start if start is not None else 0
  end = end if end is not None else file_size - 1
  log.info(f"Streaming: [{start} ~ {end}]")

  status_code = 200 if end-start == file_size else 206
  response_headers = {
    'Content-Type': 'audio/mpeg',
    'Accept-Ranges': 'bytes',
    'Content-Range': f'bytes {start}-{end}/{file_size}',
    'Content-Length': str(end-start+1)
  }
  return StreamingResponse(
    await asyncio.to_thread(audio_stream_generator, channel_name, start, end),
    status_code=status_code,
    headers=response_headers
  )




########################  Generator  ########################
async def audio_stream_generator(channel_name, start:int, end:int):
  file_path = channels[channel_name]['chunk_queue'].my_peek(0)

  with open(file_path, 'rb') as file:
    file.seek(start)
    chunk_size = CHUNK_SIZE

    while True:
      if end is not None and file.tell() + chunk_size > end:
        chunk_size = end - file.tell() + 1
      data = file.read(chunk_size)

      if not data:
        break
      yield data
