import os
import shutil

from config import STREAMING_CHANNELS, PLAYLIST_DIR, HLS_DIR

def dir_setup(channel_name):
  channel_path = os.path.join(STREAMING_CHANNELS, channel_name)
  playlist_path = os.path.join(channel_path, PLAYLIST_DIR)
  hls_path = os.path.join(channel_path, HLS_DIR)

  os.makedirs(channel_path, exist_ok=True)
  os.makedirs(playlist_path, exist_ok=True)
  create_or_clear_directory(hls_path)
  return channel_path, playlist_path, hls_path

def create_or_clear_directory(path):
  if os.path.exists(path):
    shutil.rmtree(path)
  os.makedirs(path, exist_ok=True)
