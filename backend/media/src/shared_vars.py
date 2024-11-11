import os

from segmenter import generate_segment
from dir_utils import dir_setup
from logger import log

# Shared dictionary to store channel information
channels = {}

def add_channel(channel_name):
  log.info(f"Adding channel [{channel_name}]")
  channel_path, playlist_path, hls_path = dir_setup(channel_name)

  # Start a single ffmpeg process for the entire playlist
  process = generate_segment(hls_path, playlist_path, channel_path)
  if process is None:
    log.error("Failed to start FFmpeg process.")
    return None

  channels[channel_name] = {
    'channel_path': channel_path,
    'hls_path': hls_path,
    'playlist_path': playlist_path,
    'ffmpeg_process': process
  }

  return channels[channel_name]
