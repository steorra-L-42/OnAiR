import os
import asyncio

from segment_queue import SegmentQueue
from segmenter import m3u8_setup, generate_segment
from dir_utils import dir_setup
from logger import log

channels = {}

def add_channel(channel_name):
  log.info(f"채널을 추가합니다 [{channel_name}]")

  # 디렉토리 생성
  channel_path, playlist_path, hls_path = dir_setup(channel_name)

  # 세그먼트 생성 (파일 정렬 추가)
  last_index = 0
  mp3_files = sorted([f for f in os.listdir(playlist_path) if f.endswith('mp3')])
  for file in mp3_files:
    last_index = generate_segment(hls_path, os.path.join(playlist_path, file), last_index)

  # 채널 정보 설정
  channels[channel_name] = {
    'queue': SegmentQueue(hls_path),
    'channel_path': channel_path,
    'hls_path': hls_path,
    'playlist_path': playlist_path,
    'lock': asyncio.Lock()
  }

  # .m3u8 생성
  m3u8_setup(channels[channel_name], channel_name)

  return channels[channel_name]