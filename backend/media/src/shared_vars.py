# 외부 패키지
import os

# 내부 패키지
from segment_queue import SegmentQueue
from segmenter import m3u8_setup
from dir_utils import dir_setup

from logger import log
from segmenter import generate_segment

######################  공유 변수 초기화  ######################
channels = {}




######################  채널 추가  ######################
def add_channel(channel_name):
  log.info(f"채널을 추가합니다 [{channel_name}]")
  # 디렉토리 생성
  channel_path, playlist_path, hls_path = dir_setup(channel_name)
  
  # 세그먼트 생성
  last_index = 0 # 새로 생긴 채널의 last_index는 0부터 시작
  for file in os.listdir(playlist_path):
    if file.endswith('mp3'):
      last_index = generate_segment(hls_path, os.path.join(playlist_path, file), last_index)

  # 세그먼트 큐 생성
  channels[channel_name] = {
    'queue': SegmentQueue(hls_path, last_index), # 방금 만든 세그먼트들 넣기
    'channel_path': channel_path,
    'hls_path': hls_path,
    'playlist_path': playlist_path
  }
  
  # .m3u8 생성
  m3u8_setup(channels[channel_name], channel_name)

  # m3u8 업데이트 스레드 동작
  return channels[channel_name]