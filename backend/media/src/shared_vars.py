# 외부 패키지
import os
import threading

# 내부 패키지
from config import PLAYLIST_PATH
from file_queue import FileQueue
from logger import log

channels = {}

def add_channel(channel_name):
  log.info(f"채널을 추가합니다 [{channel_name}]")
  playlist_path = os.path.join(PLAYLIST_PATH, channel_name)
  queue = FileQueue(playlist_path)

  channels[channel_name] = {
    'chunk_queue': queue,
    'file_size': queue.get_first_file_size()
  }
  return channels[channel_name]