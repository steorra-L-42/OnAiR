import asyncio
from contextlib import asynccontextmanager

# 외부 패키지
from fastapi import FastAPI
from fastapi.responses import JSONResponse, FileResponse
from fastapi.exceptions import HTTPException
from fastapi.middleware.cors import CORSMiddleware
import os

# 내부 패키지
from config import BASIC_CHANNEL_NAME, STREAMING_CHANNELS, HLS_DIR
from logger import log

from shared_vars import add_channel, channels
from segmenter import update_m3u8

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
  basic_channel = add_channel(BASIC_CHANNEL_NAME)
  basic_channel['update_task'] = asyncio.create_task(update_m3u8(basic_channel))
  log.info(f"서버 초기화 끝(채널 추가 완료) [{BASIC_CHANNEL_NAME}]")

  yield
  log.info("서버 종료 루틴 시작")
  del channels
  log.info("서버 종료")
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
  log.info(f"요청 [{segment}]")
  segment_path = os.path.join(STREAMING_CHANNELS, stream_name, HLS_DIR, segment)
  if not os.path.exists(segment_path):
    raise HTTPException(status_code=404, detail="Segment not found")
  return FileResponse(segment_path)