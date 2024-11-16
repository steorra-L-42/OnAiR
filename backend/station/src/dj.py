import json
import logging

from instance import producer


class DJ:
    def __init__(self, channel, playback_queue):
        self.channel = channel
        self.playback_queue = playback_queue

    def produce_channel_start(self, channel_id):
        medias_path = "../medias"

        start_filepath = medias_path + "/" + "start.mp3"
        file_info_list = [{
            "file_path": str(start_filepath),
            "type": "start"
        }]

        value = json.dumps({
            "file_info": file_info_list,
            "is_start": True,
            "channel_name": self.channel.channel_name,
            "fcm_token": self.channel.fcm_token
        })
        producer.send_message("media_topic",
                              channel_id.encode("utf-8"),
                              value.encode("utf-8"))

    def produce_contents(self, file_info_list, is_start=False):
        """콘텐츠를 Kafka에 송출"""
        value = json.dumps({
            "file_info": file_info_list,
            "is_start": is_start
        }, ensure_ascii=False)
        producer.send_message("media_topic",
                              self.channel.channel_id.encode("utf-8"),
                              value.encode("utf-8"))

    def stop(self):
        """DJ 종료"""
        logging.info(f"Stopping DJ for channel {self.channel.channel_id}")
