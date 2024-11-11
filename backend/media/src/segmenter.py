import subprocess
import os
from logger import log
from config import SEGMENT_DURATION
import threading

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

  return process
