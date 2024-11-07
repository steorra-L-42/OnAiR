# 외부 패키지
import os
import threading

# 내부 패키지
from config import PLAYLIST_PATH
from chunk_queue import ChunkQueue
from logger import log


channels = {}

def add_channel(channel_name):
  log.info(f"채널을 추가합니다 [{channel_name}]")
  playlist_path = os.path.join(PLAYLIST_PATH, channel_name)
  channels[channel_name] = {
    'chunk_queue': ChunkQueue(playlist_path),
    'watch_path': playlist_path,
    'stop_event': threading.Event(),
    'etc': 'init'
  }
  return channels[channel_name]