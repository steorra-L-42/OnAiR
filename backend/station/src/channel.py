import logging
from concurrent.futures import ThreadPoolExecutor
from datetime import datetime

from content_provider import ContentProvider
from dj import DJ
from dynamic_schedule_manager import DynamicScheduleManager
from music_downloader import download_from_keyword
from play_back_queue import PlaybackQueue


class Channel:
    def __init__(self, channel_id, config):
        # 필드 정의
        self.channel_id = channel_id
        self.start_time = datetime.now()
        self.is_default = config.get("isDefault")
        self.tts_engine = config.get("ttsEngine")
        self.personality = config.get("personality")
        self.news_topic = config.get("newsTopic")

        self.playlist_config = config.get("playList", [])
        self.playback_queue = PlaybackQueue()
        self.content_provider = ContentProvider(self, self.playback_queue)
        self.dj = DJ(self, self.playback_queue)
        self.schedule_manager = DynamicScheduleManager(self, self.content_provider, self.playback_queue, self.dj)
        logging.info(f"Channel {channel_id} initialized.")

    def start(self):
        """채널 시작 - 스케줄 매니저 실행 및 플레이리스트 다운로드"""
        logging.info(f"Channel {self.channel_id} is starting. Now channel will download playlist.")

        # 플레이리스트 다운로드
        self.download_playlist(self.playlist_config)

    def download_playlist(self, playlist_config):
        """플레이리스트 다운로드 및 추가"""
        with ThreadPoolExecutor(max_workers=4) as executor:
            futures = []
            for index, item in enumerate(playlist_config):
                title = item.get("playListMusicTitle")
                artist = item.get("playListMusicArtist")
                if title and artist:
                    futures.append(executor.submit(download_from_keyword, title, artist, self.channel_id, "playlists"))

            # 비동기적으로 반환된 경로들을 playlist에 추가
            for future in futures:
                file_path = future.result()  # 작업 결과가 반환되면
                if file_path:  # 결과가 None이 아닌 경우에만 추가
                    self.add_to_playlist(file_path)

    def add_to_playlist(self, filepath):
        """플레이리스트에 파일 추가"""
        self.playback_queue.playlist.append(filepath)
        logging.info(f"Added '{filepath}' to playlist")

    def stop(self):
        """채널 종료 및 모든 관련 리소스 정리"""
        logging.info(f"Channel {self.channel_id} is stopping...")

        # 1. PlaybackQueue 종료 (먼저 큐를 비우고 중지)
        if hasattr(self.playback_queue, 'stop'):
            self.playback_queue.stop()
            logging.info("PlaybackQueue stopped.")

        # 2. ContentProvider 종료 (큐가 중지된 후에 중지)
        if hasattr(self.content_provider, 'stop'):
            self.content_provider.stop()
            logging.info("ContentProvider stopped.")

        # 3. DJ 종료
        if hasattr(self.dj, 'stop'):
            self.dj.stop()
            logging.info("DJ stopped.")

        # 4. 스케줄러 종료
        if hasattr(self.schedule_manager, 'stop'):
            self.schedule_manager.stop()
            logging.info("Scheduler stopped.")

        logging.info(f"Channel {self.channel_id} has been successfully stopped.")
