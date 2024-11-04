import os
import threading
from contextlib import asynccontextmanager
from fastapi import FastAPI
from config import STREAMING_CH_DIR, ch_path, hls_output_dir, sources_dir

from channel_manager import add_channel, cleanup_channels
import gloval_vars as vars

# 서버 시작/ 종료시 실행할 옵션
@asynccontextmanager
async def lifespan(app: FastAPI):
  setup_vars()
  setup_directory()
  setup_channels()
  yield
  print("시스템 종료")
  vars.stop_event.set()
  cleanup_channels();

# 전역 변수 초기화
def setup_vars():
  vars.streams = {}
  vars.stop_event = threading.Event()

# 디렉토리 초기화
def setup_directory():
  if not os.path.exists(STREAMING_CH_DIR):
    os.makedirs(STREAMING_CH_DIR)

  for i in range(1, 4):
    if not os.path.exists(ch_path[i]):
      os.makedirs(ch_path[i])
      os.makedirs(os.path.join(ch_path[i], hls_output_dir))
      os.makedirs(os.path.join(ch_path[i], sources_dir))

# 채널 초기화
def setup_channels():
  print("기본 채널을 생성합니다.")
  channels = [
    ("channel_1", os.path.join(STREAMING_CH_DIR, "channel_1/sources"))
    # ("channel_2", os.path.join(STREAMING_CH_DIR, "channel_2/sources"))
  ]
  for channel, playlist_dir in channels:
    add_channel(channel, playlist_dir)