import os
import subprocess
import time
from intervaltree import IntervalTree
from pathlib import Path

from logger import log
from file_util import validate_file
from config import SEGMENT_DURATION, SEGMENT_UPDATE_INTERVAL, \
  SEGMENT_UPDATE_SIZE, MEDIA_FILE_PATH, MEDIA_MUSIC_TITLE, STREAMING_CHANNELS
import Stream


######################  여러 파일 -> 세그먼트  ######################
def generate_segment_from_files(hls_path, file_info_list, start):
  metadata = IntervalTree()

  for file_info in file_info_list:
    if not validate_file(file_info.get(MEDIA_FILE_PATH)):
      continue

    # 세그먼트 파일 마지막 인덱스+1 값을 얻어옴(범위 때문에)
    next_start = generate_segment(hls_path, file_info)
    if next_start is 0:
      continue

    # 시작 인덱스 ~ 마지막 인덱스 범위를 key로 metadata 저장
    metadata[start: next_start] = file_info
    start = next_start
  return metadata, next_start




######################  파일 -ffmpeg-> 세그먼트  ######################
def generate_segment(hls_path, file_info):
  dummy_path = f"{hls_path}/dummy.m3u8"
  music_title = file_info.get(MEDIA_MUSIC_TITLE)
  channel_name = Path(hls_path).parent.name
  log.info(f"[{channel_name}] 세그먼트 생성 시작 - '{music_title}'")

  ffmpeg_command = [
    'ffmpeg',
    '-loglevel', 'verbose',

    '-i', file_info.get(MEDIA_FILE_PATH),
    '-i', f"{STREAMING_CHANNELS}/empty.mp3",
    '-filter_complex', '[0:a][1:a]concat=n=2:v=0:a=1[aout]',
    '-map', '[aout]',

    '-c:a', 'aac',
    '-b:a', '128k',

    # HLS 세그먼트 옵션
    '-f', 'hls',
    '-hls_time', str(SEGMENT_DURATION),
    '-hls_list_size', '0',
    '-hls_flags', 'append_list',
    '-hls_segment_type', 'mpegts',
    '-hls_segment_filename', os.path.join(hls_path, f'segment_%06d.ts'),
    dummy_path
  ]

  process = subprocess.Popen(
    ffmpeg_command,
    stdout=subprocess.PIPE,
    stderr=subprocess.PIPE,
    universal_newlines=True,
    encoding='utf-8'
  )
  stdout, stderr = process.communicate()

  try:
    with open(dummy_path, mode="rb") as f:
      f.seek(-33, 2)
      last_line = f.readline().decode('utf-8').strip()
      end = int(last_line[8:14])
  except Exception as e:
    log.error(e)

  if process.returncode == 0:
    log.info(f"[{channel_name}] 세그먼트 생성 완료 - '{music_title}'")
    return end+1
  else:
    log.error(f"[{channel_name}] 세그먼트 생성 실패 - '{music_title}'")
    log.error(e)
    return 0




######################  m3u8 작성  ######################
def write_m3u8(stream: Stream, segments:list, m3u8_path):
  # m3u8 파일 내용 생성
  m3u8_lines = [
    "#EXTM3U\n",
    "#EXT-X-VERSION:3\n",
    f"#EXT-X-TARGETDURATION:{SEGMENT_DURATION}\n",
    f'#EXT-X-MEDIA-SEQUENCE:{segments[0]:06d}\n'
  ]
  m3u8_lines.extend(get_m3u8_seg_list(stream, segments))

  # m3u8 파일 작성
  with open(m3u8_path, "w") as f:
    f.writelines(m3u8_lines)
  wait_time = (m3u8_lines[4].strip())[8:-1]
  return float(wait_time)




###################### m3u8 작성: segment list 가져오기 ######################
def get_m3u8_seg_list(stream: Stream, segments:list):
  playlist_lines = []
  previous_index = segments[0]

  # 등록할 세그먼트 리스트를 순회하면서 파일을 작성함
  for segment_index in segments:

    ### 다음 파일 전환 검사
    if not (stream.get_metadata_by_index(segment_index) & stream.get_metadata_by_index(previous_index)):
      # duration 수정
      duration = get_audio_duration(stream = stream, segment_name = playlist_lines[-1].strip())
      playlist_lines[-2] = playlist_lines[-2].replace(str(SEGMENT_DURATION), str(duration))
      ### discontinuity 추가
      playlist_lines.append("#EXT-X-DISCONTINUITY\n")

    # 세그먼트 리스트 작성
    playlist_lines.append(f"#EXTINF:{SEGMENT_DURATION},\n")
    playlist_lines.append(f"segment_{segment_index:06d}.ts\n")
    previous_index = segment_index
  return playlist_lines




###################### 세그먼트 리스트 업데이트  ######################
def update_m3u8(stream: Stream, stop_event):
  channel_path = stream.stream_path
  m3u8_path = os.path.join(channel_path, "index.m3u8")
  temp_m3u8_path = os.path.join(channel_path, "index_temp.m3u8")

  time.sleep(SEGMENT_UPDATE_INTERVAL-0.1)
  while not stop_event.is_set():
    # 루프 시작 시간 기록
    start_time = time.perf_counter()

    # 저장할 세그먼트 리스트 조회
    segments = stream.get_queue().get_buffer_list()
    segments.extend(stream.get_queue().dequeue(SEGMENT_UPDATE_SIZE))
    # m3u8 파일 갱신
    first_seg_length = write_m3u8(stream, segments, temp_m3u8_path)
    os.replace(temp_m3u8_path, m3u8_path)
    log.info(f"[{stream.name}] 스트리밍 중 - {segments}")

    # 오래된 세그먼트 삭제
    obsolete_segments = os.path.join(stream.hls_path, f"segment_{(segments[0]-1):06d}.ts")
    if os.path.exists(obsolete_segments):
      os.remove(obsolete_segments)

    # 루프 종료 시간 기록
    end_time = time.perf_counter()
    execution_time = end_time - start_time + 0.01

    # 남은 시간 계산하여 대기 (최소 대기 시간은 0으로 설정)
    sleep_time = first_seg_length - execution_time if first_seg_length > execution_time else 0
    time.sleep(sleep_time)




######################  주어진 파일의 길이(초)를 가져오는 함수  ######################
def get_audio_duration(stream: Stream, segment_name):
  segment_path = os.path.join(stream.hls_path, segment_name)
  try:
    result = subprocess.run(
      [
        'ffprobe',
        '-v', 'error',
        '-show_entries', 'format=duration',
        '-of', 'default=noprint_wrappers=1:nokey=1',
        segment_path
      ],
      stdout=subprocess.PIPE,
      stderr=subprocess.PIPE,
      universal_newlines=True,
      text=True,
      encoding='utf-8'
    )
    return float(result.stdout.strip())
  except Exception as e:
    log.error(f"파일 길이 가져오기 실패: {e}")
    return None