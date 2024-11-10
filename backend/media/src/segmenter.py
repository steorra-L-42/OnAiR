import asyncio
import os
import subprocess
import time
from threading import Lock

import aiofiles

from logger import log
from config import STREAMING_CHANNELS
from config import SEGMENT_DURATION, SEGMENT_LIST_SIZE, SEGMENT_UPDATE_INTERVAL, SEGMENT_UPDATE_SIZE



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
  await asyncio.sleep(SEGMENT_UPDATE_INTERVAL * (SEGMENT_LIST_SIZE/2))

  while True:
    await asyncio.sleep(SEGMENT_UPDATE_INTERVAL)
    segments = channel['queue'].dequeue(SEGMENT_UPDATE_SIZE)

    # 새 세그먼트 추가
    async with aiofiles.open(m3u8_path, 'a') as f:
      previous_index = None
      for index, number in segments:
        if previous_index is not None and previous_index != index:
          await f.write("#EXT-X-DISCONTINUITY\n")
        await f.write(f"#EXTINF:{SEGMENT_DURATION},\n")
        await f.write(f"segment_{index:04d}_{number:05d}.ts\n")
        previous_index = index

    # 오래된 세그먼트 삭제
    await remove_old_segments(m3u8_path)

    # SEQUENCE 값 변경
    async with aiofiles.open(m3u8_path, 'r+') as f:
      for line_number in range(8):
        line = await f.readline()
      await f.seek(74)  # 포맷 고정 조심
      await f.write(f'{line[13:18]}'.ljust(5))
      await f.flush()

    log.info(f"m3u8 파일 업데이트 완료 [{segments}]")


# ######################  세그먼트 리스트 업데이트(오래된거 삭제)  ######################
async def remove_old_segments(m3u8_path, max_segments=5):
  try:
    async with aiofiles.open(m3u8_path, 'r') as f:
      header = [await f.readline() for _ in range(4)]
      lines = await f.readlines()

    segment_count = len(lines)/2
    if segment_count > SEGMENT_LIST_SIZE:                          # 오래된 세그먼트가 max_segments를 초과하는 경우 삭제
      new_lines = lines[-SEGMENT_LIST_SIZE * 2:]                   # 최신 max_segments만 남기고 삭제
      async with aiofiles.open(m3u8_path, 'w') as f:    # 파일을 다시 작성 (앞의 4줄 + 최신 세그먼트)
        await f.writelines(header)
        await f.writelines(new_lines)

      log.info(f"오래된 세그먼트 삭제 완료 [{segment_count - max_segments}]")

  except Exception as e:
    log.error(f"세그먼트 삭제 오류: {e}")
