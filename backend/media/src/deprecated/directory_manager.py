import os
import shutil

from logger import get_logger
from config import STREAMING_CHANNEL_PATH, HLS_OUTPUT_DIR, SOURCES_DIR, LOG_FILES_PATH

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

  log_file_path = os.path.join(LOG_FILES_PATH, f'{stream_name}_log.txt')
  if os.path.isfile(log_file_path) and os.path.getsize(log_file_path) == 0:
    logger.info(f"로그 출력 X -> 삭제 [{log_file_path}]")
    os.remove(log_file_path)



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



### 스트리밍한 음악 파일 정리 ###
def clean_used_source_files(hls_output_path):
  concat_file_path = os.path.join(hls_output_path, 'concat.txt')
  if not os.path.exists(concat_file_path):
    return

  sources = []
  with open(concat_file_path, 'r', encoding='utf-8') as concat_file:
    for line in concat_file:
      source_path = line.strip("file '")[1][:-1]
      sources.append(source_path)
  for src_path in sources:
    if os.path.exists(src_path):
      os.remove(src_path)




