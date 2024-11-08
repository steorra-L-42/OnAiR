# # 외부 패키지
# import os.path
#
# # 내부 패키지: 설정 변수/ 전역 변수
# from config import HLS_DELETE_THRESHOLD, HLS_TIME, HLS_LIST_SIZE
#
# # 내부 패키지: 기타
# from logger import get_logger
# from directory_manager import extract_stream_name
#
# # 로거 설정
# logger = get_logger()
#
#
#
# ### concat 파일 생성 ###
# def create_concat_file(hls_output_path, playlist_path):
#   # 오디오 파일 리스트 확인
#   audio_files = sorted([f for f in os.listdir(playlist_path) if f.endswith(('.mp3', '.m4a', '.aac'))])
#   logger.info(f'발견된 오디오 파일 [{audio_files}]')
#   if not audio_files:
#     raise FileNotFoundError(f"오디오 파일이 없습니다({playlist_path})")
#
#   # 파일 작성
#   concat_file_path = os.path.join(hls_output_path, 'concat.txt')
#   with open(concat_file_path, 'w', encoding='utf-8') as f:
#     for track in audio_files:
#       track_path = os.path.abspath(os.path.join(playlist_path, track))
#       f.write(f"file '{track_path}'\n")
#
#   logger.info("concat 작성 완료")
#   return concat_file_path
#
#
#
#
# ### concat 파일 업데이트 ###
# def update_concat_file(hls_output_path, new_src_path_list):
#   concat_file_path = os.path.join(hls_output_path, 'concat.txt')
#   with open(concat_file_path, 'w') as f:
#     for path in new_src_path_list:
#       f.write(f"file '{path}'\n")
#
#
#
#
# ### FFmpeg HLS 프로토콜 명령어 조회 ###
# def get_ffmpeg_command(hls_output_path, concat_file_path):
#   return [
#     'ffmpeg',
#     '-loglevel', 'error',
#     '-re',
#     # '-stream_loop', '-1',   # 입력 파일 무한 반복
#     '-f', 'concat',
#     '-safe', '0',
#     '-i', concat_file_path,
#     '-c:a', 'aac',
#     '-b:a', '128k',
#     '-f', 'hls',
#     '-hls_time', str(HLS_TIME),
#     '-hls_list_size', str(HLS_LIST_SIZE),
#     '-hls_flags', 'delete_segments+append_list+program_date_time',
#     '-hls_segment_type', 'mpegts',
#     '-hls_segment_filename', os.path.join(hls_output_path, 'segment_%05d.ts'),
#     '-method', 'PUT',
#     os.path.join(hls_output_path, 'index.m3u8')
#   ]
#
#
# ### 세그먼트 관리 ###
# def manage_segments(hls_output_path, source_path, source_set):
#   try:
#     # HLS 세그먼트 정리
#     segments = sorted([f for f in os.listdir(hls_output_path) if f.endswith('.ts')])
#     cleanup_segments(segments, hls_output_path)
#
#     # 플레이리스트 업데이트
#     return update_playlist(source_path, source_set)
#
#   except Exception as e:
#     logger.error(f"세그먼트 관리 오류: {e}")
#
#
#
# ### 세그먼트 정리 ###
# def cleanup_segments(segments, hls_path):
#   if len(segments) > HLS_DELETE_THRESHOLD:
#     to_delete = segments[:-HLS_DELETE_THRESHOLD]
#     for segment in to_delete:
#       os.remove(os.path.join(hls_path, segment))
#
#
#
#
# ### 재생 목록 업데이트 ###
# def update_playlist(source_path, prv_sources):
#   current_sources = set(os.listdir(source_path))
#   new_sources = current_sources - prv_sources
#   return list(new_sources)
#
#
#
#
#
# ### 모니터링 ###
# def monitor_segments(hls_output_path):
#   index_m3u8_path = os.path.join(hls_output_path, 'index.m3u8')
#   if not os.path.exists(index_m3u8_path):
#     logger.warning(f"재생 목록 파일을 찾을 수 없음: '{index_m3u8_path}'")
#     return
#
#   try:
#     with open(index_m3u8_path, 'r') as f:
#       content = f.read()
#
#     if '#EXTM3U' not in content:
#       logger.warning(f"index.m3u8 형식이 올바르지 않습니다. [{index_m3u8_path}]")
#       return
#
#     # 재생 목록에서 유효한 .ts 세그먼트만 가져오기
#     segments = [line.strip() for line in content.splitlines() if line.strip().endswith('.ts')]
#     if not segments:
#       logger.warning(f"유효한 세그먼트가 없습니다. [{index_m3u8_path}]")
#       return
#
#     # 현재 디렉토리에 존재하는 세그먼트만 필터링
#     existing_segments = set(os.listdir(hls_output_path))
#     valid_segments = [seg for seg in segments if os.path.basename(seg) in existing_segments]
#     if not valid_segments:
#       logger.warning(f"유효한 세그먼트가 없습니다. [{hls_output_path}]")
#       return
#
#     stream_name = extract_stream_name(hls_output_path)
#     logger.info(f'스트리밍 중 [{stream_name}] : {valid_segments}')
#
#   except Exception as e:
#     logger.error(f"재생 목록 업데이트 실패: '{index_m3u8_path}' - {e}")
