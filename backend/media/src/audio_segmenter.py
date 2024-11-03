import os
import config as cf
import ffmpeg

#
# m3u8 생성
#
def create_channels_m3u8(ch_id):
  generate_segment(ch_id)

#
# mp3 -> HLS(1)
#
def generate_segment(ch_id):
  segments_path = os.path.join(cf.ch_dirs[ch_id], cf.MEDIA_SEGMENTS)
  sources_path = os.path.join(cf.ch_dirs[ch_id], cf.MEDIA_SOURCES)

  if not os.path.exists(segments_path):
    os.makedirs(segments_path)

  for f in filter(lambda x: x.endswith(".mp3"), os.listdir(sources_path)):
    source_path = os.path.join(sources_path, f)
    base_name = os.path.splitext(f)[0]
    convert_to_hls(source_path, segments_path, ch_id, base_name)


#
# MP3 -> HLS(2)
#
def convert_to_hls(input_dir, output_dir, ch_id, base_name):
  # base_name 을 이용하여 고유한 m3u8 파일과 세그먼트 파일 이름 생성
  m3u8_path = os.path.join(output_dir, f'{base_name}')

  ffmpeg.input(input_dir).output(
      os.path.join(output_dir, 'index.m3u8'),
      format='hls',
      hls_time=10,
      hls_list_size=0,
      hls_flags='delete_segments',
      hls_segment_filename= f"{output_dir}/index{ch_id}_%d.ts"
  ).run(overwrite_output=True)

  return output_dir