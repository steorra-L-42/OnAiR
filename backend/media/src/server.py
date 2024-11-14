# 외부 패키지
import asyncio
from contextlib import asynccontextmanager
from fastapi import FastAPI
from fastapi.responses import JSONResponse, FileResponse
from fastapi.exceptions import HTTPException
from fastapi.middleware.cors import CORSMiddleware
import os

# 내부 패키지
from audio_listener import create_audio_listener_consumer
from file_utils import clear_hls_path
from config import BASIC_CHANNEL_NAME, STREAMING_CHANNELS, HLS_DIR
from logger import log

from shared_vars import add_channel, channels


app = FastAPI()


######################  CORS 설정  ######################
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


######################  서버 INIT  ######################
@asynccontextmanager
async def lifespan(app: FastAPI):
  global channels
  log.info("서버 초기화 루틴 시작")
  consumer = create_audio_listener_consumer(asyncio.get_event_loop())

  yield
  log.info("서버 종료 루틴 시작")
  for channel in channels.values():
    clear_hls_path(channel)
    channel['queue'].clear()
    channel['update_task'].cancel()
    try:
      await channel['update_task'].result()
      log.info(f'채널 제거 완료 [{channel["name"]}]')
    except Exception as e:
      log.info(f'채널 제거 실패 [{channel["name"]}] - {e}')

  del channels
  consumer.stop_event.set()
  consumer.close()
  log.info("컨슈머 정지 완료, 서버 종료")

app.router.lifespan_context = lifespan


######################  API: 채널 목록 조회  ######################
@app.get("/api/streams")
async def get_streams():
  return JSONResponse({
    "status": "success",
    "streams": list(channels.keys())
  })


######################  API: 채널의 세그먼트 큐 조회  ######################
@app.get("/api/queue")
async def get_streams():
  return JSONResponse({
    "status": "success",
    "streams": channels["channel_1"]["queue"].get_all_segments()
})


######################  API: .m3u8 파일 조회  ######################
@app.get("/channel/{stream_name}/index.m3u8")
async def serve_playlist(stream_name: str):
  m3u8_path = os.path.join(STREAMING_CHANNELS, stream_name, "index.m3u8")
  if not os.path.exists(m3u8_path):
    raise HTTPException(status_code=404, detail="Playlist not found")

  response = FileResponse(m3u8_path)
  response.headers["Cache-Control"] = "no-cache"
  return response


######################  API: 세그먼트 파일 조회  ######################
@app.get("/channel/{stream_name}/{segment}")
async def serve_segment(stream_name: str, segment: str):
  segment_path = os.path.join(STREAMING_CHANNELS, stream_name, HLS_DIR, segment)
  if not os.path.exists(segment_path):
    raise HTTPException(status_code=404, detail="Segment not found")

  response = FileResponse(segment_path)
  response.headers["Cache-Control"] = "no-cache"
  return response