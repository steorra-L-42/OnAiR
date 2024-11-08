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
from config import EMPTY_MP3, BASIC_CHANNEL_NAME, CHUNK_SIZE, CHUNK_STEP
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
  channel['update_range_task'] = asyncio.create_task(update_range(channel))
  channels[BASIC_CHANNEL_NAME] = channel

  yield
  log.info("서버 종료 루틴 시작")
  channels[BASIC_CHANNEL_NAME]['update_range_task'].cancel()

"""  서버 시작 핸들러 등록 """
app.router.lifespan_context = lifespan




########################   스트리밍 정보   ########################
@app.get("/stream-info")
async def stream_info(request: Request):
  channel_name = "channel_1"
  channel = channels[channel_name]
  return {
    'start': channel['start'],
    'end': channel['end']
  }



########################  스트리밍 엔드포인트  ########################
@app.get("/stream")
async def stream(request: Request):
  channel_name = 'channel_1'
  file_size = channels[channel_name]['file_size']
  start, end  = None, None
  status_code = 200

  range_header = request.headers.get("range")
  if range_header:
    range_values = range_header.replace("bytes=", "").split("-")
    status_code = 206
    if len(range_values) == 2:
      start = int(range_values[0])
      end = int(range_values[1]) if range_values[1] else file_size - 1

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
  file_path = channels[channel_name]['file_queue'].my_peek(0)
  with open(file_path, 'rb') as file:
    file.seek(start)
    chunk_size = end-start+1
    tmp = start

    while True:
      if end is not None and file.tell() + chunk_size > end:
        chunk_size = end - file.tell() + 1
      data = file.read(chunk_size)

      log.info(f"Streaming [{tmp} ~ {tmp+chunk_size}]")
      tmp += chunk_size

      if not data:
        break
      yield data





########################  라이브 청크 관리  ########################
async def update_range(channel):
  request_range = (CHUNK_SIZE * CHUNK_STEP) - 1
  base_wait_time = (request_range+1) / 16000
  wait_time = base_wait_time

  while True:
    await asyncio.sleep(wait_time)
    remaining_bytes = channel['file_size'] - channel['end']

    if remaining_bytes == 0:
      channel['file_size'] = channel['file_queue'].next()
      channel['start'] = 0
      channel['end'] = request_range if request_range <= channel['file_size'] else channel['file_size']
      wait_time = base_wait_time
      log.info("첫 번째 청크 스트리밍 해야 함")

    elif remaining_bytes < request_range:
      channel['start'] = channel['end']+1
      channel['end'] += remaining_bytes
      wait_time = remaining_bytes / 16000
      log.info("마지막 청크(byte)입니다.")

    else:
      channel['start'] = channel['end']+1
      channel['end'] += request_range
      wait_time = base_wait_time
      log.info(f"중간 청크입니다 [{channel['start']} ~ {channel['start']+request_range}]")


