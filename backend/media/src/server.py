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

  log.info("Server initialization started")
  basic_channel = add_channel(BASIC_CHANNEL_NAME)
  if basic_channel is None:
    log.error("Failed to initialize the basic channel.")
    raise RuntimeError("Failed to initialize the basic channel.")

  log.info(f"Server initialization complete [Channel: {BASIC_CHANNEL_NAME}]")

  yield

  log.info("Server shutdown started")
  for channel in channels.values():
    process = channel.get('ffmpeg_process')
    if process:
      process.terminate()
      process.wait()
  log.info("Server shutdown complete")

app.router.lifespan_context = lifespan

@app.get("/api/streams")
async def get_streams():
  return JSONResponse({
    "status": "success",
    "streams": list(channels.keys())
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

  response = FileResponse(segment_path)
  response.headers["Cache-Control"] = "no-cache"
  return response
