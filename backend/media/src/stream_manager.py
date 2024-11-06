# 외부 패키지
import os.path
import subprocess
import time

# 내부 패키지: 설정 변수/ 전역 변수
import gloval_vars as vars
from config import hls_output_dir, STREAMING_CH_DIR, HLS_TIME, HLS_LIST_SIZE, HLS_DELETE_THRESHOLD

# 내부 패키지: 기타
from logger import get_logger, log_function_call
from directory_manager import clean_hls_output
from hls_processor import create_concat_file, manage_segments

logger = get_logger()

#
# HLS 스트림 생성
@log_function_call
def generate_hls_stream(stream_name, playlist_path):
  hls_output_path = os.path.join(STREAMING_CH_DIR, stream_name, hls_output_dir)
  os.makedirs(hls_output_path, exist_ok=True)

  while stream_name in vars.streams:
    try:
      # 예전 파일 삭제
      clean_hls_output(hls_output_path)

      # concat 파일 생성
      concat_file_path = create_concat_file(hls_output_path, playlist_path)

      # FFmpeg 프로세스 시작
      process = start_ffmpeg_stream_process(stream_name, hls_output_path, concat_file_path)

      # 프로세스 관리
      monitor_stream_process(stream_name, process)

    except Exception as e:
      logger.error(f"스트림 생성 오류 [{stream_name}] : {e}")

    finally:
      terminate_stream_process(stream_name)

    time.sleep(5)  # 재시작 전 대기 시간


#
# FFmpeg 스트림 프로세스 생성
def start_ffmpeg_stream_process(stream_name, hls_output_path, concat_file_path):
  ffmpeg_command = [
    'ffmpeg',
    '-re',
    '-stream_loop', '-1',   # 입력 파일 무한 반복
    '-f', 'concat',
    '-safe', '0',
    '-i', concat_file_path,
    '-c:a', 'aac',
    '-b:a', '128k',
    '-f', 'hls',
    '-hls_time', str(HLS_TIME),
    '-hls_list_size', str(HLS_LIST_SIZE),
    '-hls_flags', 'delete_segments+append_list+program_date_time',
    '-hls_segment_type', 'mpegts',
    '-hls_segment_filename', os.path.join(hls_output_path, 'segment_%05d.ts'),
    '-method', 'PUT',
    os.path.join(hls_output_path, 'index.m3u8')
  ]

  logger.info(f"FFmpeg 프로세스 시작 [{stream_name}]")
  process = subprocess.Popen(
    ffmpeg_command,
    stdout = subprocess.PIPE,
    stderr = subprocess.PIPE,
    universal_newlines=True,
    encoding='utf-8'
  )

  vars.streams[stream_name]['process'] = process
  return process


#
# FFmpeg 프로세스 모니터링 & 세그먼트 관리
@log_function_call
def monitor_stream_process(stream_name, process):
  while stream_name in vars.streams and process.poll() is None:
    handle_ffmpeg_output(process, stream_name)
    manage_segments(os.path.join(STREAMING_CH_DIR, stream_name, hls_output_dir))
    time.sleep(1)
  else:
    error = process.stderr.read()
    logger.error(f'FFmpeg 프로세스 종료 [{stream_name}] : {error}')


#
# 스트림 프로세스 종료
def terminate_stream_process(stream_name=None):
  logger.info(f'스트림 프로세스를 종료합니다 [{stream_name}]')
  if not 'process' in vars.streams[stream_name]:
    return

  process = vars.streams[stream_name]['process']
  try:
    if process.poll() is None:  # 프로세스가 실행 중인 경우
      process.terminate()
      process.wait(timeout=5)
  except subprocess.TimeoutExpired:
    process.kill()
    logger.error(f"프로세스 강제 종료 [{stream_name if stream_name else 'unknown stream'}]")
  except Exception as e:
    logger.error(f"프로세스 종료 실패 [{stream_name if stream_name else 'unknown stream'}] : {e}")


#
# 프로세스 모니터링 관리
def handle_ffmpeg_output(process, stream_name):
  output = process.stderr.readline()
  if output == '':
    return
  # if output:
  #   logger.info(f'ffmepg [{stream_name}] : {output.strip()}')
