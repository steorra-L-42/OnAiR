import logging
from collections import deque


class PlaybackQueue:
    def __init__(self):
        self.queues = {
            "news": deque(),
            "weather": deque(),
            "story": deque()
        }
        self.playlist = []

    def log_queues(self):
        """각 큐의 원소들과 playlist의 내용을 로그로 출력"""
        logging.info("Playback queues:")

        # 큐 내용 출력
        for content_type, queue in self.queues.items():
            logging.info(f"Content type: {content_type}")
            for item in queue:
                logging.info(f"  {item}")

        # playlist 내용 출력
        logging.info("Playlist contents:")
        for idx, item in enumerate(self.playlist):
            logging.info(f"  {idx}: {item}")

    def add_content(self, content_type, file_path):
        if content_type in self.queues:
            self.queues[content_type].append(file_path)
            logging.info(f"Added to {content_type} queue: {file_path}")

    def stop(self):
        """모든 큐와 플레이리스트를 비우고 리소스 해제"""
        logging.info("Stopping PlaybackQueue and clearing all resources...")

        # 모든 큐 비우기
        for content_type, queue in self.queues.items():
            queue.clear()
            logging.info(f"Cleared {content_type} queue")

        # 플레이리스트 비우기
        self.playlist.clear()
        logging.info("Cleared playlist")
        logging.info("PlaybackQueue resources have been released.")
