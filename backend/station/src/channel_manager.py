import json
import logging
from pathlib import Path


class ChannelManager:
    MAX_CHANNELS = 5

    def __init__(self, producer):
        self.channels = {}
        self.producer = producer

    def add_channel(self, channel_id, config):
        if len(self.channels) >= self.MAX_CHANNELS:
            logging.warning(f"Cannot create more channels. Maximum limit of {self.MAX_CHANNELS} reached.")
            return

        if channel_id not in self.channels:
            from channel import Channel
            # 채널 생성
            self.channels[channel_id] = Channel(channel_id, config)

            # 시작
            self.channels[channel_id].start()

            # 채널 시작 여부 produce
            self.produce_channel_start(channel_id)
            logging.info(f"Channel {channel_id} created.")
            # 방송 시작
            self.channels[channel_id].schedule_manager.process_broadcast()

        else:
            # 채널_id 충돌.
            logging.warning(f"Channel {channel_id} already exists.")

    def produce_channel_start(self, channel_id):
        project_root = Path(__file__).resolve().parent.parent.parent
        start_filepath = project_root / "station" / "medias" / "start.mp3"
        value = json.dumps({
            "filePath": str(start_filepath),
            "isStart": True
        })
        self.producer.send_message("media_topic",
                                   channel_id.encode("utf-8"),
                                   value.encode("utf-8"))

    def remove_channel(self, channel_id):
        if channel_id in self.channels:
            # 채널 종료.
            self.channels[channel_id].stop()
            # channels dictionary에서 삭제.
            del self.channels[channel_id]
            # 카프카 레코드 전송
            self.producer.send_message("channel_close_topic",
                                       channel_id.encode("utf-8"),
                                       json.dumps({}).encode("utf-8"))
            logging.info(f"Channel {channel_id} removed. {len(self.channels)} channels left")
