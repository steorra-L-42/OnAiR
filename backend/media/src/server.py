import asyncio
from contextlib import asynccontextmanager

from fastapi import FastAPI
from fastapi.responses import JSONResponse, FileResponse
from fastapi.exceptions import HTTPException
from fastapi.middleware.cors import CORSMiddleware
import os

from config import BASIC_CHANNEL_NAME, STREAMING_CHANNELS, HLS_DIR
from logger import log
from shared_vars import add_channel, channels
from segmenter import update_m3u8

app = FastAPI()

app.add_middleware(
  CORSMiddleware,
  allow_origins=["*"],
  allow_credentials=True,
  allow_methods=["*"],
  allow_headers=["*"],
)

@asynccontextmanager
async def lifespan(app: FastAPI):
  global channels

  log.info("서버 초기화 루틴 시작")
  basic_channel = add_channel(BASIC_CHANNEL_NAME)
  basic_channel['update_task'] = asyncio.create_task(update_m3u8(basic_channel))
  log.info(f"서버 초기화 끝(채널 추가 완료) [{BASIC_CHANNEL_NAME}]")

  yield
  log.info("서버 종료 루틴 시작")
  del channels
  log.info("서버 종료")
app.router.lifespan_context = lifespan

@app.get("/api/streams")
async def get_streams():
  return JSONResponse({
    "status": "success",
    "streams": list(channels.keys())
  })

@app.get("/api/queue")
async def get_streams():
  return JSONResponse({
    "status": "success",
    "streams": channels["channel_1"]["queue"].get_all_segments()
  })

@app.get("/channel/{stream_name}/index.m3u8")
async def serve_playlist(stream_name: str):
  m3u8_path = os.path.join(STREAMING_CHANNELS, stream_name, "index.m3u8")
  if not os.path.exists(m3u8_path):
    raise HTTPException(status_code=404, detail="Playlist not found")

  response = FileResponse(m3u8_path)
  response.headers["Cache-Control"] = "no-cache"
  return response

@app.get("/channel/{stream_name}/{segment}")
async def serve_segment(stream_name: str, segment: str):
  segment_path = os.path.join(STREAMING_CHANNELS, stream_name, HLS_DIR, segment)
  if not os.path.exists(segment_path):
    raise HTTPException(status_code=404, detail="Segment not found")
  return FileResponse(segment_path)