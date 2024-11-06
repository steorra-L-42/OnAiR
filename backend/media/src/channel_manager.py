# 외부 패키지
import os.path
import threading
from logger import get_logger

# 내부 패키지: 설정 변수/ 전역 변수
import gloval_vars as vars
from config import base_url, RESET

# 내부 패키지: 기타
from stream_manager import generate_hls_stream, terminate_stream_process
from directory_manager import clean_stream

logger = get_logger()

#
# 채널 추가
def add_channel(stream_name, playlist_path):
  if stream_name in vars.streams:
    return {
      'status': 'error',
      'message': f'방송이 이미 존재합니다: {stream_name}'
    }

  if not os.path.exists(playlist_path):
    return {
      'status': 'error',
      'message': f'재생 목록 디렉터리가 존재하지 않습니다: {playlist_path}'
    }

  vars.streams[stream_name] = {}
  thread = threading.Thread(
    target=generate_hls_stream,
    args=(stream_name, playlist_path),
    daemon=True
  )
  vars.streams[stream_name]['thread'] = thread
  thread.start()

  return {
    'status': 'success',
    'message': f'방송이 추가되었습니다: {stream_name} ',
    'url': f"{base_url}/channel/{stream_name}/index.m3u8"
  }


#
# 채널 삭제
def remove_channel(stream_name):
  if stream_name not in vars.streams:
    return {
      'status': 'error',
      'message': f'{stream_name} 방송 없는데?'
    }
  # 프로세스 제거
  terminate_stream_process(stream_name)
  
  # 디렉토리 정리
  if RESET:
    clean_stream(stream_name)


#
# 채널 전체 삭제
def cleanup_channels():
  for stream_name in list(vars.streams.keys()):
    remove_channel(stream_name)