import os
from fastapi import FastAPI, HTTPException
from fastapi.responses import FileResponse
from fastapi.middleware.cors import CORSMiddleware
import config as cf
import audio_segmenter

app = FastAPI()

#
# CORS 설정
#
app.add_middleware(
  CORSMiddleware,
  allow_origins=["http://localhost:3000"],  # React 앱이 실행되는 URL
  allow_credentials=True,
  allow_methods=["*"],
  allow_headers=["*"],
)


#
# endpoint: m3u8
#
@app.get("/ch{ch_id}")
async def getPlaylist(ch_id: int):
  m3u8_path = cf.m3u8_dirs[ch_id]
  if not os.path.exists(m3u8_path):
    audio_segmenter.create_channels_m3u8(ch_id)

  return FileResponse(
    path=m3u8_path,
    media_type="application/vnd.apple.mpegurl",
    filename="index.m3u8"
  )


#
# endpoint: ts
#
@app.get("/index{ch_id}_{ts_id}.ts")
def get_ts(ch_id: int, ts_id: int):
  ts_path = os.path.join(cf.ch_dirs[ch_id], f"media_segments\index{ch_id}_{ts_id}.ts")
  if not os.path.exists(ts_path):
    print(f"not found: {ts_path}")
    raise HTTPException(status_code=404, detail="TS file not found")

  return FileResponse(path=ts_path, media_type="video/MP2T")
