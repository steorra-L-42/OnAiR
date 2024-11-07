import logging
from concurrent.futures import ThreadPoolExecutor
from queue import Queue

from content_provider import ContentProvider
from dj import Dj
from src.music_downloader import download_from_keyword


class Channel:
    def __init__(self, channel_id, config):
        # 필드 정의
        self.channel_id = channel_id
        self.is_default = config.get("isDefault")
        self.tts_engine = config.get("ttsEngine")
        self.personality = config.get("personality")
        self.news_topic = config.get("newsTopic")

        # DJ, ContentProvider 생성
        self.dj = Dj(self)
        self.content_provider = ContentProvider(self)

        # 방송 목록 초기화
        playlist_config = config.get("playList", [])
        self.playlist = [None] * len(playlist_config)
        self.playlist_number = 0  # PlayList 재생 위치 관리
        self.weathers = Queue()
        self.news = Queue()
        self.stories = Queue()

        # PlayList 경로 추가
        self.download_playlist(playlist_config)

        logging.info(f"Weathers: {self.weathers}, News: {self.news}, Stories: {self.stories}")
        logging.info(self.playlist);

    # 플레이리스트 다운
    def download_playlist(self, playlist_config):

        with ThreadPoolExecutor(max_workers=4) as executor:
            for index, item in enumerate(playlist_config):
                title = item.get("playListMusicTitle")
                artist = item.get("playListMusicArtist")
                if title and artist:
                    # 각 다운로드 작업을 스레드에 제출
                    executor.submit(download_from_keyword, title, artist, index, self)

    def add_to_playlist(self, filepath, index):
        self.playlist[index] = filepath
        logging.info(f"Added '{filepath}' to playlist at index {index}")

    def stop(self):
        logging.info("Release channel resources")
