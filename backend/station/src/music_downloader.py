import logging
import os
from pathlib import Path

from pytubefix import YouTube
from youtubesearchpython import VideosSearch


def download_from_keyword(title, artist, index, channel):
    keyword = f"{title} - {artist}"
    # 유튜브에서 키워드로 검색하여 가장 상단의 결과를 가져옴
    videos_search = VideosSearch(keyword, limit=1)
    result = videos_search.result()

    # 검색 결과가 없는 경우 처리
    if not result['result']:
        logging.info("No videos found for the keyword.")
        return

    # 최상단 비디오의 URL 가져오기
    video_info = result['result'][0]
    video_url = video_info['link']

    # 출력 파일 이름 설정
    safe_filename = "".join(c if c.isalnum() or c in ["-", " "] else "" for c in keyword)

    # medias 디렉토리의 절대 경로 설정 (프로젝트 루트 기준)
    project_root = Path(__file__).resolve().parent.parent.parent  # backend 디렉토리의 상위
    output_filepath = project_root / "station" / "medias" / channel.channel_id / "playlists"
    output_filename = output_filepath / f"{safe_filename}.mp3"

    # 경로가 존재하지 않으면 생성
    os.makedirs(output_filepath, exist_ok=True)

    # 이미 파일이 존재하면 다운로드를 건너뜁니다.
    if output_filename.exists():
        logging.info(f"File '{output_filename}' already exists, skipping download.")
        return

    # pytubefix로 YouTube 오디오 다운로드
    try:
        yt = YouTube(video_url)
        audio_stream = yt.streams.filter(only_audio=True).first()
        if audio_stream:
            audio_stream.download(output_path=str(output_filepath), filename=f"{safe_filename}.mp3")
            channel.add_to_playlist(str(output_filename), index)
            logging.info(f"Downloaded audio as '{output_filename}'")
        else:
            logging.info("No audio stream available for this video.")
    except Exception as e:
        logging.error(f"An error occurred: {e}")