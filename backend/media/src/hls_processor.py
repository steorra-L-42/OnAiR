# 외부 패키지
import os.path

# 내부 패키지: 설정 변수/ 전역 변수
import gloval_vars as vars
from config import HLS_DELETE_THRESHOLD

# 내부 패키지: 기타
from logger import logger


#
# concat 파일 생성
def create_concat_file(hls_output_path, playlist_path):
  concat_file_path = os.path.join(hls_output_path, 'concat.txt')
  audio_files = sorted([f for f in os.listdir(playlist_path) if f.endswith(('.mp3', '.m4a', '.aac'))])
  if not audio_files:
    logger.error(f"오디오 파일이 없습니다: {playlist_path}")
    return None

  with open(concat_file_path, 'w', encoding='utf-8') as f:
    for track in audio_files:
      track_path = os.path.abspath(os.path.join(playlist_path, track))
      f.write(f"file '{track_path}'\n")
  return concat_file_path


# 
# 세그먼트 관리
def manage_segments(hls_output_path):
  try:
    segments = sorted([f for f in os.listdir(hls_output_path) if f.endswith('.ts')])
    if len(segments) > HLS_DELETE_THRESHOLD:
      to_delete = segments[:-HLS_DELETE_THRESHOLD]
      for segment in to_delete:
        try:
          os.remove(os.path.join(hls_output_path, segment))
        except OSError:
          pass
    update_playlist(hls_output_path)
  except Exception as e:
    logger.error(f"세그먼트 관리 오류: {e}")


# 
# 재생 목록 업데이트
def update_playlist(hls_output_path: str):
  index_m3u8_path = os.path.join(hls_output_path, 'index.m3u8')
  if not os.path.exists(index_m3u8_path):
    logger.warning(f"재생 목록 파일을 찾을 수 없음: '{index_m3u8_path}'")
    return

  try:
    with open(index_m3u8_path, 'r') as f:
      content = f.read()

    if '#EXTM3U' not in content:
      logger.warning(f"파일 형식이 올바르지 않음 (#EXTM3U 없음): '{index_m3u8_path}'")
      return

    # 재생 목록에서 유효한 .ts 세그먼트만 가져오기
    segments = [line.strip() for line in content.splitlines() if line.strip().endswith('.ts')]
    if not segments:
      logger.warning(f"유효한 세그먼트 없음 (.ts 파일 없음): '{index_m3u8_path}'")
      return

    # 현재 디렉토리에 존재하는 세그먼트만 필터링
    existing_segments = set(os.listdir(hls_output_path))
    valid_segments = [seg for seg in segments if os.path.basename(seg) in existing_segments]
    if not valid_segments:
      logger.warning(f"유효한 세그먼트 없음: '{hls_output_path}'")
      return

    # 성공 로그
    logger.info(f"재생 목록 업데이트 성공: '{index_m3u8_path}'")

  except Exception as e:
    logger.error(f"재생 목록 업데이트 실패: '{index_m3u8_path}' - {e}")