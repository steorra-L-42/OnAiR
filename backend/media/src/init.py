# 외부 패키지
import os
import threading
from contextlib import asynccontextmanager
from fastapi import FastAPI

# 내부 패키지: 설정
import config
from gloval_vars import streams

# 내부 패키지: 기타
from channel_manager import add_channel, cleanup_channels
from music_downloader import process_and_download_video
from logger import get_logger, log_function_call

logger = get_logger()



# 서버 시작, 종료시 실행할 옵션
@asynccontextmanager
async def lifespan(app: FastAPI):
  logger.info("시스템이 실행됩니다.")
  setup_vars()
  setup_directory()
  setup_channels()

  yield
  logger.info("시스템이 종료됩니다.")
  cleanup_channels();



### 전역 변수 초기화 ###
def setup_vars():
  streams = {}



### 디렉토리 초기화 ###
def setup_directory():
  if not os.path.exists(config.STREAMING_CHANNEL_PATH):
    os.makedirs(config.STREAMING_CHANNEL_PATH)
    os.makedirs(config.LOG_FILES_PATH, exist_ok=True)

  for i in range(1, 4):
    ch_path = config.CHANNEL_PATHS[i]
    if not os.path.exists(ch_path):
      os.makedirs(ch_path)                                        # 채널 디렉토리
      os.makedirs(os.path.join(ch_path, config.HLS_OUTPUT_DIR))   # HLS 파일 저장
      os.makedirs(os.path.join(ch_path, config.SOURCES_DIR))      # 소스 음성 저장



### 채널 초기화 ###
@log_function_call("기본 채널 셋팅을 시작합니다.")
def setup_channels():

  # 기본 채널 정보 셋업
  basic_ch_no = config.BASIC_CHANNEL_NO
  channels = [(
    config.BASIC_CHANNEL_NAMES[basic_ch_no],                              # 채널 이름
    os.path.join(config.CHANNEL_PATHS[basic_ch_no], config.SOURCES_DIR)   # 채널 소스 음성 경로
  )]

  # 기본 채널 생성
  for channel, playlist_dir in channels:
    logger.info(f"기본 채널 음악 초기화 여부 [{config.BASIC_CHANNEL_INIT_MUSIC}]")
    if config.BASIC_CHANNEL_INIT_MUSIC:
      setup_music(playlist_dir)
    add_channel(channel, playlist_dir)



### 기본 음악 초기화 ###
def setup_music(source_path):
  urls = [
    'https://www.youtube.com/watch?v=9E6b3swbnWg',
    'https://www.youtube.com/watch?v=7Fc2_w9HPmc'
  ]
  for url in urls:
    process_and_download_video(url, source_path)