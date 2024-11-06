import os
import shutil

from logger import get_logger
from config import STREAMING_CHANNEL_PATH, HLS_OUTPUT_DIR, SOURCES_DIR

logger = get_logger()



### HLS 프로토콜 파일 정리 (삭제) ###
def clean_hls_output(hls_output_path):
  for file in os.listdir(hls_output_path):
    if file.endswith(('.ts', '.m3u8', '.aac')):
      os.remove(os.path.join(hls_output_path, file))



### 채널 스트리밍 이후 디렉토리 [정리] ###
def clean_stream(stream_name):
  logger.info(f"채널 디렉토리 정리 [{stream_name}]")
  base = os.path.join(STREAMING_CHANNEL_PATH, stream_name)
  remove_stream_path(os.path.join(base, HLS_OUTPUT_DIR))
  remove_stream_path(os.path.join(base, SOURCES_DIR))



### 채널 스트리밍 이후 디렉토리 [삭제] ###
def remove_stream_path(stream_path):
  if os.path.exists(stream_path):
    shutil.rmtree(stream_path)
  os.makedirs(stream_path)



### 경로에서부터 stream_name 추출 ###
def extract_stream_name(hls_output_path):
  path_parts = os.path.normpath(hls_output_path).split(os.sep)
  if 'streaming_channels' in path_parts:
    stream_index = path_parts.index('streaming_channels') + 1
    if stream_index < len(path_parts):
      return path_parts[stream_index]
  return None