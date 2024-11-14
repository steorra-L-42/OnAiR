import json
import logging
from pathlib import Path

from instance import producer


class DJ:
    def __init__(self, channel, playback_queue):
        self.channel = channel
        self.playback_queue = playback_queue

    def produce_channel_start(self, channel_id):
        src_path = Path(__file__).resolve().parent

        start_filepath = src_path / "medias" / "start.mp3"
        value = json.dumps({
            "filePath": str(start_filepath),
            "isStart": True
        })
        producer.send_message("media_topic",
                              channel_id.encode("utf-8"),
                              value.encode("utf-8"))

    def produce_contents(self, file_paths):
        """콘텐츠를 Kafka에 송출"""
        value = json.dumps({
            "filePath": file_paths,
            "isStart": False
        }, ensure_ascii=False)
        producer.send_message("media_topic",
                              self.channel.channel_id.encode("utf-8"),
                              value.encode("utf-8"))

    def stop(self):
        """DJ 종료"""
        logging.info(f"Stopping DJ for channel {self.channel.channel_id}")
