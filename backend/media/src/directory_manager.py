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


def extract_stream_name(hls_output_path):
  # 경로를 디렉터리로 분리하여 stream_name 추출
  path_parts = os.path.normpath(hls_output_path).split(os.sep)

  # 경로에서 'streaming_channels' 뒤의 값을 stream_name으로 사용
  if 'streaming_channels' in path_parts:
    stream_index = path_parts.index('streaming_channels') + 1
    if stream_index < len(path_parts):
      return path_parts[stream_index]
  return None