import asyncio
import os
import subprocess
import time
from threading import Lock

import aiofiles

from logger import log
from config import STREAMING_CHANNELS
from config import SEGMENT_DURATION, SEGMENT_LIST_SIZE, SEGMENT_UPDATE_INTERVAL, SEGMENT_UPDATE_SIZE
from config import INDEX_DISC_CHAR_NUM, INDEX_INF_CHAR_NUM, INDEX_SEGMENT_CHAR_NUM

def generate_segment(hls_path, file_path, last_index):
  log.info(f'Starting segment generation [{file_path}] (Index: {last_index})')

  segment_pattern = os.path.join(hls_path, "segment_%03d.ts")

  ffmpeg_command = [
    'ffmpeg',
    '-loglevel', 'info',
    '-i', file_path,
    '-c:a', 'aac',
    '-b:a', '128k',
    '-f', 'segment',
    '-segment_time', str(SEGMENT_DURATION),
    '-segment_format', 'mpegts',
    '-segment_list', os.path.join(STREAMING_CHANNELS, "channel_1/index.m3u8"),
    segment_pattern
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
    log.info(f"Segment generation completed [{file_path}] (Index: {last_index})")
  else:
    log.error(f"Segment generation failed [{file_path}] (Index: {last_index})\n{stderr}")

  return last_index + 1

def m3u8_setup(channel, channel_name):
  m3u8_path = os.path.join(STREAMING_CHANNELS, channel_name, "index.m3u8")
  with open(m3u8_path, "w") as f:
    f.write("#EXTM3U\n")
    f.write("#EXT-X-VERSION:3\n")
    f.write(f"#EXT-X-TARGETDURATION:{int(SEGMENT_DURATION) + 1}\n")

    # Don't add #EXT-X-ENDLIST to keep the playlist live
    segments = channel['queue'].dequeue(SEGMENT_LIST_SIZE)

    if not segments:
      log.error(f"No segments available for channel {channel_name}; .m3u8 may be empty.")
      return

    f.write(f"#EXT-X-MEDIA-SEQUENCE:{segments[0][1]:05d}\n")
    for index, number in segments:
      f.write(f"#EXTINF:{SEGMENT_DURATION},\n")
      f.write(f"segment_{index:03d}.ts\n")

  log.info(f"index.m3u8 created for channel [{channel_name}] with {len(segments)} segments.")

async def update_m3u8(channel):
  channel_path = channel['channel_path']
  m3u8_path = os.path.join(channel_path, "index.m3u8")

  # Keep track of the media sequence number
  media_sequence = 0
  max_segments_in_playlist = 10  # Maintain a sliding window of segments

  await asyncio.sleep(SEGMENT_DURATION)

  while True:
    await asyncio.sleep(SEGMENT_UPDATE_INTERVAL)

    async with channel.get('lock', asyncio.Lock()):
      segments = channel['queue'].get_all_segments()

      if not segments:
        continue

      # Write a complete new playlist each time
      async with aiofiles.open(m3u8_path, 'w') as f:
        await f.write("#EXTM3U\n")
        await f.write("#EXT-X-VERSION:3\n")
        await f.write(f"#EXT-X-TARGETDURATION:{int(SEGMENT_DURATION) + 1}\n")

        # Update media sequence to create a sliding window
        if len(segments) > max_segments_in_playlist:
          media_sequence = segments[-max_segments_in_playlist][1]
          segments = segments[-max_segments_in_playlist:]

        await f.write(f"#EXT-X-MEDIA-SEQUENCE:{media_sequence}\n")

        previous_index = None
        for index, number in segments:
          if previous_index is not None and previous_index != index - 1:
            await f.write("#EXT-X-DISCONTINUITY\n")

          await f.write(f"#EXTINF:{SEGMENT_DURATION},\n")
          await f.write(f"segment_{index:03d}.ts\n")
          previous_index = index

      # Clean up old segments
      await remove_old_segments(channel['hls_path'], segments)

async def remove_old_segments(hls_path, current_segments, keep_count=15):
  """Remove segments that are no longer in the playlist while keeping a buffer."""
  try:
    current_segment_files = {f"segment_{index:03d}.ts" for index, _ in current_segments}
    for file in os.listdir(hls_path):
      if file.endswith('.ts') and file not in current_segment_files:
        try:
          os.remove(os.path.join(hls_path, file))
          log.info(f"Removed old segment: {file}")
        except Exception as e:
          log.error(f"Error removing segment {file}: {e}")
  except Exception as e:
    log.error(f"Error during segment cleanup: {e}")