import yt_dlp
from youtubesearchpython import VideosSearch
import os

def download_top_audio(keyword):
  # 유튜브에서 키워드로 검색하여 가장 상단의 결과를 가져옴
  videos_search = VideosSearch(keyword, limit=1)
  result = videos_search.result()

  # 검색 결과가 없는 경우 처리
  if not result['result']:
    print("No videos found for the keyword.")
    return

  # 최상단 비디오의 URL 가져오기
  video_info = result['result'][0]
  video_url = video_info['link']
  video_title = video_info['title']

  # 출력 파일 이름 설정
  output_filename = f"{video_title}.mp3"

  # 파일 이름이 중복되지 않도록 공백과 특수문자 처리
  safe_filename = "".join(c if c.isalnum() else "_" for c in video_title)
  output_filepath = f"{safe_filename}.mp3"

  # 이미 파일이 존재하면 다운로드를 건너뜁니다.
  if os.path.exists(output_filepath):
    print(f"File '{output_filepath}' already exists, skipping download.")
    return

  print(f"Downloading audio for: {video_title}")
  print(f"URL: {video_url}")

  # yt-dlp를 사용하여 오디오 다운로드
  ydl_opts = {
    'format': 'bestaudio/best',
    'outtmpl': output_filepath,
    'postprocessors': [{
      'key': 'FFmpegExtractAudio',
      'preferredcodec': 'mp3',
      'preferredquality': '192',
    }],
  }

  # 파일이 이미 mp3 형식인 경우, postprocessor를 제거
  if output_filepath.endswith(".mp3"):
    ydl_opts.pop('postprocessors', None)

  with yt_dlp.YoutubeDL(ydl_opts) as ydl:
    ydl.download([video_url])

  print("Audio download complete.")

# 예시 실행
download_top_audio("카더가든 우리의 밤을 외워요")
download_top_audio("성시경 차마")
download_top_audio("카더가든 아무렇지 않은 사람")
download_top_audio("양다일 고백")
