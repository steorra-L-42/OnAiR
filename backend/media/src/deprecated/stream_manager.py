# 외부 패키지
import os.path
import subprocess
import time

# 내부 패키지: 설정 변수/ 전역 변수
from gloval_vars import streams
from config import STREAMING_CHANNEL_PATH, HLS_OUTPUT_DIR, LOG_FILES_PATH, SOURCES_DIR

# 내부 패키지: 기타
from logger import get_logger
from directory_manager import clean_hls_output
from hls_processor import create_concat_file, manage_segments, get_ffmpeg_command, monitor_segments

# 로거 설정
logger = get_logger()



### HLS 스트림 생성 ###
def generate_hls_stream(stream_name, playlist_path):
  hls_output_path = os.path.join(STREAMING_CHANNEL_PATH, stream_name, HLS_OUTPUT_DIR)
  os.makedirs(hls_output_path, exist_ok=True)

  try:
    # 예전 파일 삭제
    clean_hls_output(hls_output_path)

    # concat 파일 생성
    concat_file_path = create_concat_file(hls_output_path, playlist_path)

    # FFmpeg 프로세스 시작
    process = start_ffmpeg_stream_process(stream_name, hls_output_path, concat_file_path)

    # 프로세스 관리
    monitor_ffmpeg_stream_process(stream_name, process)

  except Exception as e:
    logger.error(f"스트림 생성 오류 [{stream_name}] : {e}")

  finally:
    terminate_ffmpeg_stream_process(stream_name)



### FFmpeg 스트림 프로세스 생성 ###
def start_ffmpeg_stream_process(stream_name, hls_output_path, concat_file_path):
  ## 커맨드 설정 ##
  ffmpeg_command = get_ffmpeg_command(hls_output_path, concat_file_path)

  ## 프로세스 로그 파일 생성 ##
  log_file_path = os.path.join(LOG_FILES_PATH, f'{stream_name}_log.txt')
  os.makedirs(os.path.dirname(log_file_path), exist_ok=True)

  ## 프로세스 시작 ##
  logger.info(f"FFmpeg 스트림 프로세스 시작 [{stream_name}]")
  with open(log_file_path, 'w', encoding='utf-8') as log_file:
    process = subprocess.Popen(
      ffmpeg_command,
      stdout=subprocess.DEVNULL,
      stderr=log_file,      # stderr를 log_file에 직접 기록
      universal_newlines=True,
      encoding='utf-8'
    )
  streams[stream_name]['process'] = process
  return process



### FFmpeg 프로세스 모니터링 & 세그먼트 관리 ###
def monitor_ffmpeg_stream_process(stream_name, process):
  while stream_name in streams and process.poll() is None:
    hls_output_path = os.path.join(STREAMING_CHANNEL_PATH, stream_name, HLS_OUTPUT_DIR)
    source_path = os.path.join(STREAMING_CHANNEL_PATH, stream_name, SOURCES_DIR)

    manage_segments(hls_output_path, source_path, set())
    monitor_segments(hls_output_path)
    time.sleep(1)
  else:
    logger.error(f'스트림 종료 [{stream_name}]')



### 스트림 프로세스 종료 ###
def terminate_ffmpeg_stream_process(stream_name=None):
  logger.info(f'ffmpeg 스트림 프로세스 종료 [{stream_name}]')
  if not 'process' in streams[stream_name]:
    return

  process = streams[stream_name]['process']
  try:
    if process.poll() is None:  # 프로세스가 실행 중인 경우
      process.terminate()
      process.wait(timeout=5)
  except subprocess.TimeoutExpired:
    process.kill()
    logger.error(f"프로세스 강제 종료 [{stream_name if stream_name else 'unknown stream'}]")
  except Exception as e:
    logger.error(f"프로세스 종료 실패 [{stream_name if stream_name else 'unknown stream'}] : {e}")
