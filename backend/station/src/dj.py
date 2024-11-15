import json
import logging
import os

from instance import producer
from path_util import get_medias_path


class DJ:
    def __init__(self, channel, playback_queue):
        self.channel = channel
        self.playback_queue = playback_queue

    def produce_channel_start(self, channel_id):
        current_dir = os.getcwd()
        medias_path = get_medias_path(current_dir)

        start_filepath = medias_path / "start.mp3"
        value = json.dumps({
            "filePath": str(start_filepath),
            "isStart": True
        })
        producer.send_message("media_topic",
                              channel_id.encode("utf-8"),
                              value.encode("utf-8"))

    def produce_contents(self, file_info_list, is_start=False):
        """콘텐츠를 Kafka에 송출"""
        value = json.dumps({
            "fileInfo": file_info_list,
            "isStart": is_start
        }, ensure_ascii=False)
        producer.send_message("media_topic",
                              self.channel.channel_id.encode("utf-8"),
                              value.encode("utf-8"))

    def stop(self):
        """DJ 종료"""
        logging.info(f"Stopping DJ for channel {self.channel.channel_id}")
