import os
from contextlib import asynccontextmanager
from fastapi import FastAPI

#
# 디렉토리 관련 변수 정의
#
ROOT_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__), '..'))
STREAMING_CH_DIR = os.path.join(ROOT_DIR, "streaming_channels")
MEDIA_SEGMENTS = "media_segments"
MEDIA_SOURCES = "media_sources"

ch_dirs = [os.path.join(STREAMING_CH_DIR, f"channel{i}") for i in range(4)]
m3u8_dirs = [os.path.join(ch_dirs[i], "media_segments/master.m3u8") for i in range(4)]


#
# 서버가 시작할 때 디렉토리 셋팅
#
@asynccontextmanager
async def lifespan(app: FastAPI):
  if not os.path.exists(STREAMING_CH_DIR):
    os.makedirs(STREAMING_CH_DIR)

  for i in range(1, 4):
    if not os.path.exists(ch_dirs[i]):
      os.makedirs(ch_dirs[i])
      os.makedirs(os.path.join(ch_dirs[i], "media_segments"))
      os.makedirs(os.path.join(ch_dirs[i], "media_sources"))
  yield