import os
from fastapi import FastAPI, HTTPException
from fastapi.responses import FileResponse
from fastapi.middleware.cors import CORSMiddleware
import ffmpeg

app = FastAPI()

# CORS 설정
app.add_middleware(
  CORSMiddleware,
  allow_origins=["http://localhost:3000"],  # React 앱이 실행되는 URL
  allow_credentials=True,
  allow_methods=["*"],
  allow_headers=["*"],
)

# 오디오 파일 경로
MEDIA_SOURCES = "./media_sources"
MEDIA_SEGMENTS = "./media_segments"
DEFAULT_FILE_NAME = "test"
FILE_RANGE = range(1, 5)  # test1.mp3부터 test4.mp3까지

# HLS 세그먼트 생성 함수
def convert_to_hls(file_path: str, output_dir: str):
  hls_output_path = output_dir
  if not os.path.exists(hls_output_path):
    os.makedirs(hls_output_path, exist_ok=True)  # 세그먼트 디렉토리 생성

  ffmpeg.input(file_path).output(
    os.path.join(hls_output_path, 'index.m3u8'),
    format='hls',
    hls_time=10,
    hls_list_size=0,
    hls_flags='delete_segments',
    hls_segment_filename=os.path.join(hls_output_path, 'ch1', 'index%d.ts')
  ).run(overwrite_output=True)

  return hls_output_path

# 마스터 플레이리스트 생성 함수
def create_master_playlist():
  master_playlist_path = os.path.join(MEDIA_SEGMENTS, "master_playlist.m3u8")

  with open(master_playlist_path, "w") as playlist:
    playlist.write("#EXTM3U\n")

    for i in FILE_RANGE:
      hls_path = os.path.join(MEDIA_SEGMENTS, f"{DEFAULT_FILE_NAME}{i}")
      m3u8_file = os.path.join(hls_path, "index.m3u8")

      # HLS 세그먼트를 확인하고, 필요 시 생성
      if not os.path.exists(m3u8_file):
        generate_hls_segments(i, hls_path)

      # 플레이리스트 항목 추가
      playlist.write(generate_playlist_entry(i))

  return master_playlist_path

def generate_hls_segments(index, hls_path):
  """
  HLS 세그먼트가 없을 때 MP3 파일을 HLS 세그먼트로 변환하는 함수
  """
  file_path = os.path.join(MEDIA_SOURCES, f"{DEFAULT_FILE_NAME}{index}.mp3")
  if os.path.exists(file_path):
    convert_to_hls(file_path, hls_path)

def generate_playlist_entry(index):
  """
  마스터 플레이리스트에 추가할 개별 m3u8 파일 경로와 EXTINF 설정을 반환하는 함수
  """
  entry = f"#EXTINF:-1,\n"
  entry += f"{DEFAULT_FILE_NAME}{index}/index.m3u8\n"
  return entry


# 오디오 스트리밍 경로 설정
@app.get("/stream")
async def stream_audio():
  master_playlist_path = create_master_playlist()

  # 마스터 플레이리스트 파일이 존재하는지 확인
  if not os.path.exists(master_playlist_path):
    raise HTTPException(status_code=500, detail="Failed to create master playlist")

  # FileResponse에 filename과 media_type 설정
  return FileResponse(
    path=master_playlist_path,
    media_type="application/vnd.apple.mpegurl",
    filename="index.m3u8"  # 사용자에게 표시될 파일 이름
  )

@app.get("/ch1")
async def ch_audio():
  index_m3u8_path = f"./media_segments/test1/index.m3u8"

  # 마스터 플레이리스트 파일이 존재하는지 확인
  if not os.path.exists(index_m3u8_path):
    raise HTTPException(status_code=500, detail="Failed to create master playlist")

  # FileResponse에 filename과 media_type 설정
  return FileResponse(
    path=index_m3u8_path,
    media_type="application/vnd.apple.mpegurl",
    filename="index.m3u8"  # 사용자에게 표시될 파일 이름
  )

@app.get("/ch1/{ts_id}")
async def ch_audio(ts_id: str):
  ts_path = f"./media_segments/test1/{ts_id}"
  print(ts_path)

  # 파일이 존재하는지 확인
  if not os.path.exists(ts_path):
    raise HTTPException(status_code=404, detail="Segment file not found")

  # .ts 파일을 video/mp2t MIME 타입으로 반환
  return FileResponse(
    path=ts_path,
    media_type="video/mp2t"
  )