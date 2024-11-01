import os
from fastapi import FastAPI, HTTPException
from fastapi.responses import FileResponse
from fastapi.middleware.cors import CORSMiddleware
import ffmpeg

app = FastAPI()

#
# CORS 설정
#
app.add_middleware(
  CORSMiddleware,
  allow_origins=["http://localhost:3000"],  # React 앱이 실행되는 URL
  allow_credentials=True,
  allow_methods=["*"],
  allow_headers=["*"],
)


#
# 각종 변수
#
MEDIA_SOURCES = "./media_sources"     # 음성 파일 위치
MEDIA_SEGMENTS = "./media_segments"   # hls 파일 위치
DEFAULT_FILE_NAME = "test"            # 음성 파일 기본 이름


#
# 채널별 엔드포인트
#
@app.get("/ch{ch_id}")
async def ch_audio(ch_id: int):
  master_playlist_path = f"./media_segments/master_playlist{ch_id}.m3u8"
  if not os.path.exists(master_playlist_path):
    master_playlist_path = create_master_playlist(ch_id)

  return FileResponse(
    path=master_playlist_path,
    media_type="application/vnd.apple.mpegurl",
    filename="index.m3u8"  # 사용자에게 표시될 파일 이름
  )


#
# 특정 채널의 master m3u8 파일 생성/ 경로 반환
#
def create_master_playlist(ch_id):
  master_m3u8_path = os.path.join(MEDIA_SEGMENTS, f"master_playlist{ch_id}.m3u8")
  
  with open(master_m3u8_path, "w") as playlist:
    playlist.write("#EXTM3U\n")
    
    for i in range(1, ch_id+1):

      # 세그먼트가 있는 디렉토리와 index.m3u8 파일 위치 지정
      channel_hls_path = os.path.join(MEDIA_SEGMENTS, f"channel{ch_id}")
      index_m3u8_path = os.path.join(channel_hls_path, "index.m3u8")

      # 세그먼트 유무 검사
      # 세그먼트가 없으면 해당 인덱스의 mp3파일로 생성
      if not os.path.exists(index_m3u8_path):
        generate_hls_segment(f"{DEFAULT_FILE_NAME}{i}.mp3", channel_hls_path)

      # 플레이리스트에 항목 추가
      playlist.write(generate_playlist_entry(i))
      
  return master_m3u8_path

#
# 특정 채널의 HLS segments 파일들이 없을 경우
# MP3를 segments로 변환함
#
def generate_hls_segment(source_file_name, channel_hls_path):
  source_file_path = os.path.join(MEDIA_SOURCES, source_file_name)
  if os.path.exists(source_file_path):
    convert_to_hls(source_file_path, channel_hls_path)


#
# MP3 -> HLS
#
def convert_to_hls(source_file_path, channel_hls_path):
  if not os.path.exists(channel_hls_path):
    os.makedirs(channel_hls_path, exist_ok=True)

  ffmpeg.input(source_file_path).output(
    os.path.join(channel_hls_path, 'index.m3u8'),
    format='hls',
    hls_time=10,
    hls_list_size=0,
    hls_flags='delete_segments',
    hls_segment_filename=os.path.join(channel_hls_path, 'index%d.ts')
  ).run(overwrite_output=True)

  return channel_hls_path


#
#  마스터 플레이리스트에 추가할 개별 m3u8 파일 경로와 EXTINF 설정을 반환하는 함수
#
def generate_playlist_entry(index):
  entry = f"#EXTINF:-1,\n"
  entry += f"channel{index}/index.m3u8\n"
  return entry