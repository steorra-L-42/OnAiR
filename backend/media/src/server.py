
# 외부 패키지
from fastapi import FastAPI, HTTPException, Request
from fastapi.responses import JSONResponse, FileResponse
from fastapi.middleware.cors import CORSMiddleware
import os

# 내부 패키지: 설정
from config import STREAMING_CHANNEL_PATH, HLS_OUTPUT_DIR, CLIENT_BASE_URL
from gloval_vars import streams
import init

# 내부 패키지: 기타
from channel_manager import add_channel, remove_channel

app = FastAPI(lifespan=init.lifespan)


### CORS 설정 ###
app.add_middleware(
  CORSMiddleware,
  allow_origins=[CLIENT_BASE_URL],
  allow_credentials=True,
  allow_methods=["*"],
  allow_headers=["*"],
)



### API 엔드포인트: 스트림 목록 조회 ###
@app.get("/api/streams")
async def get_streams():
  return JSONResponse({
    "status": "success",
    "streams": list(streams.keys())
  })



### API 엔드포인트: 방송 시작 ###
@app.post("/api/streams/{stream_name}")
async def start_stream(stream_name: str, request: Request):
  body = await request.json()
  playlist_dir = body.get("playlist_dir")
  if not playlist_dir:
    return JSONResponse({
      "status": "error",
      "message": "playlist_dir 필수"
    }, status_code=400)

  result = add_channel(stream_name, playlist_dir)
  return JSONResponse(result)



### API 엔드포인트: 방송 종료 ###
@app.delete("/api/streams/{stream_name}")
async def stop_stream(stream_name: str):
  result = remove_channel(stream_name)
  return JSONResponse(result)



### 채널 재생 목록 서비스 ###
@app.get("/channel/{stream_name}/index.m3u8")
async def serve_playlist(stream_name: str):
  playlist_path = os.path.join(STREAMING_CHANNEL_PATH, stream_name, HLS_OUTPUT_DIR, "index.m3u8")
  if not os.path.exists(playlist_path):
    raise HTTPException(status_code=404, detail="Playlist not found")
  response = FileResponse(playlist_path)
  response.headers["Cache-Control"] = "no-cache"
  return response



### 채널 세그먼트 서비스 ###
@app.get("/channel/{stream_name}/{segment}")
async def serve_segment(stream_name: str, segment: str):
  segment_path = os.path.join(STREAMING_CHANNEL_PATH, stream_name, HLS_OUTPUT_DIR, segment)
  if not os.path.exists(segment_path):
    raise HTTPException(status_code=404, detail="Segment not found")
  return FileResponse(segment_path)
