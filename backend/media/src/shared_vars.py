# 외부 패키지
import os
import asyncio

# 내부 패키지
from logger import log
from config import SEGMENT_LIST_SIZE

from segment_queue import SegmentQueue
from segmenter import generate_segment_from_files, write_m3u8, update_m3u8
from dir_utils import dir_setup

######################  공유 변수 초기화  ######################
channels = {}




######################  채널 추가  ######################
def add_channel(channel_name, file_info_list, loop):
  # 디렉토리 생성
  channel_path, playlist_path, hls_path = dir_setup(channel_name)
  log.info(f"채널을 추가합니다 [{channel_name}] at [{channel_path}]")
  
  # 초기 세그먼트 생성
  last_index = generate_segment_from_files(
    hls_path,
    file_info_list,
    last_index = 0
  )

  # channels 변수에 추가
  # 세그먼트 큐 생성 및 초기화(기본 세그먼트 삽입)
  channels[channel_name] = {
    'name': channel_name,
    'queue': SegmentQueue(hls_path, last_index), # 방금 만든 세그먼트들 넣기
    'channel_path': channel_path,
    'hls_path': hls_path,
    'playlist_path': playlist_path
  }
  channel = channels[channel_name]

  # .m3u8 생성
  write_m3u8(
    channel,
    os.path.join(channel_path, 'index.m3u8'),
    channel['queue'].dequeue(SEGMENT_LIST_SIZE)
  )

  # m3u8 update task 생성
  channel['update_task'] = asyncio.run_coroutine_threadsafe(update_m3u8(channel), loop)
  return channels[channel_name]