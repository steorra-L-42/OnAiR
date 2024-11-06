# 외부 패키지
import os.path

# 내부 패키지: 설정 변수/ 전역 변수
import gloval_vars as vars
from config import HLS_DELETE_THRESHOLD

# 내부 패키지: 기타
from logger import get_logger
from directory_manager import extract_stream_name

# 로거 설정
logger = get_logger()



### concat 파일 생성 ###
def create_concat_file(hls_output_path, playlist_path):
  concat_file_path = os.path.join(hls_output_path, 'concat.txt')
  audio_files = sorted([f for f in os.listdir(playlist_path) if f.endswith(('.mp3', '.m4a', '.aac'))])
  if not audio_files:
    raise FileNotFoundError(f"오디오 파일이 없습니다({playlist_path})")

  with open(concat_file_path, 'w', encoding='utf-8') as f:
    for track in audio_files:
      track_path = os.path.abspath(os.path.join(playlist_path, track))
      f.write(f"file '{track_path}'\n")
  return concat_file_path



### 세그먼트 관리 ###
def manage_segments(hls_output_path):
  try:
    # HLS 세그먼트 파일 목록 정렬
    segments = sorted([f for f in os.listdir(hls_output_path) if f.endswith('.ts')])

    # 세그먼트가 많이 쌓이면 정리함
    cleanup_segments(segments, hls_output_path)

    # 플레이리스트 업데이트
    update_playlist(hls_output_path)

  except Exception as e:
    logger.error(f"세그먼트 관리 오류: {e}")



### 세그먼트 정리 ###
def cleanup_segments(segments, hls_path):
  if len(segments) > HLS_DELETE_THRESHOLD:
    to_delete = segments[:-HLS_DELETE_THRESHOLD]
    for segment in to_delete:
      os.remove(os.path.join(hls_path, segment))



### 재생 목록 업데이트 ###
def update_playlist(hls_output_path: str):
  index_m3u8_path = os.path.join(hls_output_path, 'index.m3u8')
  if not os.path.exists(index_m3u8_path):
    logger.warning(f"재생 목록 파일을 찾을 수 없음: '{index_m3u8_path}'")
    return

  try:
    with open(index_m3u8_path, 'r') as f:
      content = f.read()

    if '#EXTM3U' not in content:
      logger.warning(f"index.m3u8 형식이 올바르지 않습니다. [{index_m3u8_path}]")
      return

    # 재생 목록에서 유효한 .ts 세그먼트만 가져오기
    segments = [line.strip() for line in content.splitlines() if line.strip().endswith('.ts')]
    if not segments:
      logger.warning(f"유효한 세그먼트가 없습니다. [{index_m3u8_path}]")
      return

    # 현재 디렉토리에 존재하는 세그먼트만 필터링
    existing_segments = set(os.listdir(hls_output_path))
    valid_segments = [seg for seg in segments if os.path.basename(seg) in existing_segments]
    if not valid_segments:
      logger.warning(f"유효한 세그먼트가 없습니다. [{hls_output_path}]")
      return

    stream_name = extract_stream_name(hls_output_path)
    logger.info(f'스트리밍 중 [{stream_name}] : {valid_segments}')

  except Exception as e:
    logger.error(f"재생 목록 업데이트 실패: '{index_m3u8_path}' - {e}")