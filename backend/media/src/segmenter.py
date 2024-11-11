import subprocess
import os
from logger import log
<<<<<<< HEAD
from config import STREAMING_CHANNELS
from config import SEGMENT_DURATION, SEGMENT_LIST_SIZE, SEGMENT_UPDATE_INTERVAL, SEGMENT_UPDATE_SIZE
=======
from config import SEGMENT_DURATION
import threading
>>>>>>> e43bd87f290631fc01d39d94da56452bc6464f29

def generate_segment(hls_path, playlist_path, channel_path):
  log.info(f'Generating segments from playlist [{playlist_path}]')

  # Prepare the list of input MP3 files with absolute paths
  mp3_files = [os.path.abspath(os.path.join(playlist_path, f)) for f in sorted(os.listdir(playlist_path)) if f.endswith('.mp3')]
  if not mp3_files:
    log.error("No MP3 files found in the playlist directory.")
    return None

  concatenated_mp3 = os.path.join(channel_path, 'concatenated.mp3')

  # Create the inputs.txt file for concatenation
  inputs_txt_path = os.path.join(channel_path, 'inputs.txt')
  with open(inputs_txt_path, 'w') as f:
    for mp3_file in mp3_files:
      # Write the absolute path to inputs.txt
      f.write(f"file '{mp3_file}'\n")

  # Concatenate the MP3 files into one
  concat_command = [
    'ffmpeg',
    '-y',
    '-f', 'concat',
    '-safe', '0',
    '-i', inputs_txt_path,
    '-c', 'copy',
    concatenated_mp3
  ]

  log.info('Concatenating MP3 files...')
  concat_process = subprocess.run(concat_command, stdout=subprocess.PIPE, stderr=subprocess.PIPE, universal_newlines=True)
  if concat_process.returncode != 0:
    log.error(f"Failed to concatenate MP3 files:\n{concat_process.stderr}")
    return None

  log.info('MP3 files concatenated successfully.')

  # Start the FFmpeg process to generate HLS segments with looping
  ffmpeg_command = [
    'ffmpeg',
    '-loglevel', 'info',
    '-re',
    '-stream_loop', '-1',
    '-i', concatenated_mp3,
    '-c:a', 'aac',
    '-b:a', '128k',
    '-f', 'hls',
    '-hls_time', str(SEGMENT_DURATION),
    '-hls_list_size', '5',
    '-hls_flags', 'delete_segments',
    '-hls_segment_filename', os.path.join(hls_path, 'segment_%05d.ts'),
    os.path.join(channel_path, 'index.m3u8')
  ]

  process = subprocess.Popen(
    ffmpeg_command,
    stdout=subprocess.PIPE,
    stderr=subprocess.PIPE,
    universal_newlines=True,
    encoding='utf-8'
  )

  # Log any errors from FFmpeg
  def log_ffmpeg_errors(proc):
    for line in proc.stderr:
      log.info(f"FFmpeg: {line.strip()}")

  # Start a background thread to read stderr
  threading.Thread(target=log_ffmpeg_errors, args=(process,), daemon=True).start()

<<<<<<< HEAD
######################  m3u8 작성  ######################
async def write_m3u8(channel, m3u8_path, segments):
  m3u8_lines = [
    "#EXTM3U\n",
    "#EXT-X-VERSION:3\n",
    f"#EXT-X-TARGETDURATION:{SEGMENT_DURATION}\n",
  ]
  # channel['queue'].dequeue(SEGMENT_LIST_SIZE)
  m3u8_lines.extend(get_m3u8_seg_list(channel, segments))

  async with aiofiles.open(m3u8_path, "w") as f:
    await f.writelines(m3u8_lines)
  wait_time = (m3u8_lines[3].strip())[8:-1]
  return float(wait_time)


###################### m3u8 작성: segment list 가져오기 ######################
def get_m3u8_seg_list(channel, segments):
  playlist_lines = []
  previous_index = segments[0][0]
  for index, number in segments:
    if previous_index != index:
      ### 다음 파일 전환, duration 수정
      duration = get_audio_duration(channel, playlist_lines[-1].strip())
      playlist_lines[-2] = playlist_lines[-2].replace(str(SEGMENT_DURATION), str(duration))
      ### discontinuity 추가
      playlist_lines.append("#EXT-X-DISCONTINUITY\n")

    # 세그먼트 리스트 작성
    playlist_lines.append(f"#EXTINF:{SEGMENT_DURATION},\n")
    playlist_lines.append(f"segment_{index:04d}_{number:05d}.ts\n")
    previous_index = index
  return playlist_lines


###################### 세그먼트 리스트 업데이트  ######################
async def update_m3u8(channel):
  channel_path = channel['channel_path']
  m3u8_path = os.path.join(channel_path, "index.m3u8")
  temp_m3u8_path = os.path.join(channel_path, "index_temp.m3u8")
  await asyncio.sleep(SEGMENT_UPDATE_INTERVAL)

  while True:
    # 루프 시작 시간 기록
    start_time = time.perf_counter()

    # 저장할 세그먼트 리스트 조회
    segments = channel['queue'].get_buffer()
    segments.extend(channel['queue'].dequeue(SEGMENT_UPDATE_SIZE))
      
    # index_temp.m3u8 작성
    first_seg_length = await write_m3u8(channel, temp_m3u8_path, segments)
    
    # 파일 교체
    try:
      os.replace(temp_m3u8_path, m3u8_path)
      log.info("index.m3u8 업데이트 완료")
    except PermissionError as e:
      await asyncio.sleep(0.2)  # 잠시 대기 후 재시도

    # 루프 종료 시간 기록
    end_time = time.perf_counter()
    execution_time = end_time - start_time

    # 남은 시간 계산하여 대기 (최소 대기 시간은 0으로 설정)
    sleep_time = first_seg_length - execution_time if first_seg_length > execution_time else 0
    print(f'함수 소요 시간 [{execution_time}]')
    print(f'세그먼트 시간 [{first_seg_length}]')
    print(f'wait to [{sleep_time}]')
    await asyncio.sleep(sleep_time)


######################  주어진 파일의 길이(초)를 가져오는 함수  ######################
def get_audio_duration(channel, segment_name):
  segment_path = os.path.join(channel['hls_path'], segment_name)
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
=======
  return process
>>>>>>> e43bd87f290631fc01d39d94da56452bc6464f29
