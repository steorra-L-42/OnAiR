import asyncio
import itertools
import os
import subprocess
import time
from threading import Lock

import aiofiles

from logger import log
from config import STREAMING_CHANNELS
from config import SEGMENT_DURATION, SEGMENT_LIST_SIZE, SEGMENT_UPDATE_INTERVAL, SEGMENT_UPDATE_SIZE
from config import INDEX_DISC_CHAR_NUM, INDEX_INF_CHAR_NUM, INDEX_SEGMENT_CHAR_NUM


######################  파일 -ffmpeg-> 세그먼트  ######################
def generate_segment(hls_path, file_path, last_index):
  log.info(f'세그먼트 생성 시작 [{file_path}]')
  ffmpeg_command = [
    'ffmpeg',
    '-loglevel', 'info',
    '-i', file_path,
    '-c:a', 'aac',
    '-b:a', '128k',
    '-f', 'hls',
    '-hls_time', str(SEGMENT_DURATION),
    '-hls_list_size', '0',
    '-hls_segment_type', 'mpegts',
    '-hls_segment_filename', os.path.join(hls_path, f'segment_{last_index:04d}_%5d.ts'),
    os.path.join(STREAMING_CHANNELS, "channel_1/dummy.m3u8")
  ]

  process = subprocess.Popen(
      ffmpeg_command,
      stdout=subprocess.PIPE,
      stderr=subprocess.PIPE,
      universal_newlines=True,
      encoding='utf-8'
  )

  stdout, stderr = process.communicate()
  if process.returncode == 0:
    log.info(f"세그먼트 생성 완료 [{file_path}]")
  else:
    log.error(f"세그먼트 생성 실패 [{file_path}\n{stderr}]")
  return (last_index+1)


######################  채널 시작시 HLS 준비  ######################
def m3u8_setup(channel, channel_name):
  m3u8_path = os.path.join(STREAMING_CHANNELS, channel_name, "index.m3u8")
  with open(m3u8_path, "w") as f:
    f.write("#EXTM3U\n")
    f.write("#EXT-X-VERSION:3\n")
    f.write(f"#EXT-X-TARGETDURATION:{SEGMENT_DURATION}\n")

    # SEGMENT_LIST_SIZE 만큼 큐에서 빼서 파일에 작성
    segments = channel['queue'].dequeue(SEGMENT_LIST_SIZE)
    f.write(f"#EXT-X-MEDIA-SEQUENCE:{segments[0][1]:05d}\n")
    for index, number in segments:
      f.write(f"#EXTINF:{SEGMENT_DURATION},\n")
      f.write(f"segment_{index:04d}_{number:05d}.ts\n")
  log.info(f"index.m3u8 생성 [{channel_name}]")


######################  세그먼트 리스트 업데이트  ######################
async def update_m3u8(channel):
  channel_path = channel['channel_path']
  m3u8_path = os.path.join(channel_path, "index.m3u8")
  temp_m3u8_path = os.path.join(channel_path, "index_temp.m3u8")

  while True:
    await asyncio.sleep(SEGMENT_UPDATE_INTERVAL)
    log.info("index.m3u8 업데이트")

    previous_index = channel['queue'].get_last()
    segments = channel['queue'].get_buffer()
    for index, number in channel['queue'].dequeue(SEGMENT_UPDATE_SIZE):
      segments.append((index, number))

    playlist_lines = [
      "#EXTM3U\n",
      "#EXT-X-VERSION:3\n",
      f"#EXT-X-TARGETDURATION:{SEGMENT_DURATION}\n",
      f"#EXT-X-MEDIA-SEQUENCE:{segments[0][1]}\n"
    ]

    # 새 세그먼트 추가
    for index, number in segments:
      if previous_index != -1 and previous_index != index: # 다음 파일 스트리밍 경우
        playlist_lines.append("#EXT-X-DISCONTINUITY\n")
      playlist_lines.append(f"#EXTINF:{SEGMENT_DURATION},\n")
      playlist_lines.append(f"segment_{index:04d}_{number:05d}.ts\n")
      previous_index = index

    # 임시 파일에 쓰기
    async with aiofiles.open(temp_m3u8_path, 'w') as f:
      await f.writelines(playlist_lines)

    # 파일이 완전히 닫힐 시간을 기다림
    await asyncio.sleep(0.1)  # 파일이 닫힐 시간을 확보

    try:
      # 기존 플레이리스트를 원자적으로 교체
      os.replace(temp_m3u8_path, m3u8_path)
      log.info("기존 플레이리스트를 원자적으로 교체 완료")
    except PermissionError as e:
      log.error(f"파일 교체 실패: {e}")
      await asyncio.sleep(0.2)  # 잠시 대기 후 재시도




# ######################  세그먼트 리스트 업데이트(오래된거 삭제)  ######################
async def remove_old_segments(m3u8_path, max_segments=5):
  try:
    async with aiofiles.open(m3u8_path, 'r') as f:
      header = [await f.readline() for _ in range(4)]
      lines = await f.readlines()

    segment_count = len(lines)/2
    if segment_count > SEGMENT_LIST_SIZE:

      update_lines = SEGMENT_UPDATE_SIZE*2
      update_lines = update_lines+1 if len(lines[update_lines]) == INDEX_DISC_CHAR_NUM else update_lines
      new_lines = lines[update_lines:]

      async with aiofiles.open(m3u8_path, 'w') as f:
        await f.writelines(header)
        await f.writelines(new_lines)

  except Exception as e:
    log.error(f"세그먼트 삭제 오류: {e}")
