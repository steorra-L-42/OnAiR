import os

from ffmpeg import output
from yt_dlp.extractor.loom import LoomIE

import config as cf
import ffmpeg

#
# m3u8 생성
#
def create_channels_m3u8(ch_id):
  generate_segment(ch_id)
  create_master_m3u8(ch_id)

#
# mp3 -> HLS(1)
#
def generate_segment(ch_id):
  segments_path = os.path.join(cf.ch_dirs[ch_id], cf.MEDIA_SEGMENTS)
  sources_path = os.path.join(cf.ch_dirs[ch_id], cf.MEDIA_SOURCES)

  if not os.path.exists(segments_path):
    os.makedirs(segments_path)

  index_no = 1
  for f in filter(lambda x: x.endswith(".mp3"), os.listdir(sources_path)):
    source_path = os.path.join(sources_path, f)
    segment_path = os.path.join(segments_path, str(index_no))

    convert_to_hls(source_path, segment_path, index_no)
    index_no += 1


#
# MP3 -> HLS(2)
#
def convert_to_hls(input_dir, output_dir, index_no):
  if not os.path.exists(output_dir):
    os.makedirs(output_dir)

  ffmpeg.input(input_dir).output(
      os.path.join(output_dir, f'index{index_no}.m3u8'),
      format='hls',
      hls_time=10,
      hls_list_size=0,
      hls_flags='delete_segments',
      hls_segment_filename= f"{output_dir}/segment_{index_no}_%d.ts"
  ).run(overwrite_output=True)

  return output_dir

#
# master.m3u8 생성
#
def create_master_m3u8(ch_id):
  segments_path = os.path.join(cf.ch_dirs[ch_id], cf.MEDIA_SEGMENTS)
  master_path = cf.m3u8_dirs[ch_id]

  with open(master_path, 'w') as master_file:
    master_file.write('#EXTM3U\n')
    for index_no in range(1, len(os.listdir(segments_path))):
      master_file.write(f'#EXT-X-STREAM-INF:BANDWIDTH=128000\n')
      master_file.write(f'ch{ch_id}\\index{index_no}.m3u8\n')