import os
from fastapi import FastAPI, HTTPException
from fastapi.responses import FileResponse
from fastapi.middleware.cors import CORSMiddleware
import config as cf
import audio_segmenter

app = FastAPI(lifespan=cf.lifespan)

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
# endpoint: master.m3u8
#
@app.get("/ch{ch_id}")
async def getMasterPlaylist(ch_id: int):
  print(f"master playlist : {ch_id}")
  master_m3u8_path = cf.m3u8_dirs[ch_id]
  if not os.path.exists(master_m3u8_path):
    audio_segmenter.create_channels_m3u8(ch_id)

  return FileResponse(
    path=master_m3u8_path,
    media_type="application/vnd.apple.mpegurl",
    filename="master.m3u8"
  )

#
# endpoint: index.m3u8
#
@app.get("/ch{ch_id}/index{index_id}.m3u8")
async def getIndexPlaylist(ch_id: int, index_id: int):
  index_m3u8_index = os.path.join(cf.ch_dirs[ch_id], cf.MEDIA_SEGMENTS, str(ch_id), f"index{index_id}.m3u8")
  if not os.path.exists(index_m3u8_index):
    print(f"not found index.m3u8: ({ch_id}, {index_id})")
    raise HTTPException(status_code=404, detail="TS file not found")

  return FileResponse(
      path=index_m3u8_index,
      media_type="application/vnd.apple.mpegurl",
      filename="master.m3u8"
  )


#
# endpoint: ts
#
@app.get("/ch{ch_id}/segment_{index_id}_{ts_id}.ts")
async def get_ts(ch_id: int, index_id: int, ts_id: int):
  ch_seg_ts_path = os.path.join(cf.ch_dirs[ch_id], cf.MEDIA_SEGMENTS, str(index_id))
  ts_path = os.path.join(ch_seg_ts_path, f'segment_{index_id}_{ts_id}.ts')

  if not os.path.exists(ts_path):
    print(f"not found: {ts_path}")
    raise HTTPException(status_code=404, detail="TS file not found")

  return FileResponse(path=ts_path, media_type="video/MP2T")
