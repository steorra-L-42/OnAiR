import yt_dlp
from youtubesearchpython import VideosSearch
import os

def download_from_keyword(keyword):

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



def get_video_info(url):
  ydl_opts = {
    'simulate': True,  # 다운로드를 수행하지 않고 정보만 가져옵니다.
    'quiet': True      # 출력 최소화
  }

  with yt_dlp.YoutubeDL(ydl_opts) as ydl:
    info = ydl.extract_info(url, download=False)  # download=False는 정보만 가져옵니다.
    return info

def process_and_download_video(url, path):
  video_info = get_video_info(url)               # 유튜브 영상 정보 읽기
  safe_filename = "".join(c if c.isalnum() else "_" for c in video_info['title'])  # 이름 처리
  filepath = os.path.join(path, safe_filename)   # 파일 위치 지정

  if not os.path.exists(filepath):
    download_youtube_video(url, filepath)


def download_youtube_video(url, filepath):
  ydl_opts = {
    'format': 'bestaudio/best',
    'outtmpl': filepath,
    'postprocessors': [{
      'key': 'FFmpegExtractAudio',
      'preferredcodec': 'mp3',
      'preferredquality': '192',
    }],
  }
  with yt_dlp.YoutubeDL(ydl_opts) as ydl:
    ydl.download([url])