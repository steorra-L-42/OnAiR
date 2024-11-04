import os
import shutil


def clean_hls_output(hls_output_path):
  for file in os.listdir(hls_output_path):
    if file.endswith(('.ts', '.m3u8', '.aac')):
      os.remove(os.path.join(hls_output_path, file))


def reset_stream_path(stream_path):
  if os.path.exists(stream_path):
    shutil.rmtree(stream_path)
  os.makedirs(stream_path)