# 외부 패키지
import os.path
import subprocess
import time

# 내부 패키지: 설정 변수/ 전역 변수
import gloval_vars as vars
from config import hls_output_dir, STREAMING_CH_DIR, HLS_TIME, HLS_LIST_SIZE, HLS_DELETE_THRESHOLD

# 내부 패키지: 기타
from logger import logger
from directory_manager import clean_hls_output
from hls_processor import create_concat_file, manage_segments

#
# HLS 스트림 생성
def generate_hls_stream(stream_name, playlist_path):
  hls_output_path = os.path.join(STREAMING_CH_DIR, stream_name, hls_output_dir)
  os.makedirs(hls_output_path, exist_ok=True)

  while not vars.stop_event.is_set() and stream_name in vars.streams:
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
      logger.error(f"스트림 생성 오류: {stream_name} - {e}")

    finally:
      terminate_stream_process(stream_name)

    # 오류 발생(재시작)
    if not vars.stop_event.is_set() and stream_name in vars.streams:
      logger.info(f"{stream_name} 스트림 오류 발생. 재시작 하는중")
      time.sleep(2)  # 재시작 전 대기 시간

#
# FFmpeg 스트림 프로세스 생성
def start_ffmpeg_stream_process(stream_name, hls_output_path, concat_file_path):
  print(concat_file_path)
  ffmpeg_command = [
    'ffmpeg',
    '-re',
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

  logger.info(f"{stream_name} 방송 FFmpeg 프로세스 시작")
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
def monitor_stream_process(stream_name, process):
  while not vars.stop_event.is_set() and stream_name in vars.streams:
    if process.poll() is not None:
      error = process.stderr.read()
      logger.error(f"{stream_name} 방송 FFmpeg 프로세스 갑자기 종료됨!!: {error}")
      break

    handle_ffmpeg_output(process, stream_name)
    manage_segments(os.path.join(STREAMING_CH_DIR, stream_name, hls_output_dir))
    time.sleep(0.1)  # 모니터링 반응성 향상을 위한 대기 시간 감소

  else :
    terminate_stream_process_without_if(process)


#
# 스트림 프로세스 종료
def terminate_stream_process(stream_name:str):
  if stream_name in vars.streams and 'process' in vars.streams[stream_name]:
    process = vars.streams[stream_name]['process']
    if process.poll() is None:
      try:
        process.terminate()
        process.wait(timeout=5)
      except subprocess.TimeoutExpired:
        process.kill()
      except Exception as e:
        logger.error(f"오류: '{stream_name}' 프로세스 종료 실패 - {e}")

def terminate_stream_process_without_if(process):
  process.terminate()
  try:
    process.wait(timeout=5)
  except subprocess.TimeoutExpired:
    process.kill()

#
# 프로세스 모니터링 관리
def handle_ffmpeg_output(process, stream_name):
  output = process.stderr.readline()
  if output:
    logger.debug(f"{stream_name} FFmpeg output: {output.strip()}")