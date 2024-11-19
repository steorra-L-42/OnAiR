import json
import logging

from config import max_channels


class ChannelManager:
    def __init__(self, producer):
        self.channels = {}
        self.producer = producer

    def add_channel(self, channel_id, config):
        if len(self.channels) >= max_channels:
            logging.warning(f"Cannot create more channels. Maximum limit of {max_channels} reached.")
            return

        if channel_id not in self.channels:
            from channel import Channel
            # 채널 생성
            channel = Channel(channel_id, config)
            self.channels[channel_id] = channel

            # 시작
            channel.start()

            logging.info(f"Channel {channel_id} created.")
            # 방송 시작
            self.channels[channel_id].process_broadcast()

        else:
            # 채널_id 충돌.
            logging.warning(f"Channel {channel_id} already exists.")

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
