import logging


class ChannelManager:
    MAX_CHANNELS = 5

    def __init__(self):
        self.channels = {}

    def add_channel(self, channel_id, config):
        if len(self.channels) >= self.MAX_CHANNELS:
            logging.warning(f"Cannot create more channels. Maximum limit of {self.MAX_CHANNELS} reached.")
            return

        if channel_id not in self.channels:
            from src.channel import Channel
            # 채널 생성
            self.channels[channel_id] = Channel(channel_id, config)

            # 시작
            self.channels[channel_id].start()
            logging.info(f"Channel {channel_id} created.")

        else:
            # 채널_id 충돌.
            logging.warning(f"Channel {channel_id} already exists.")

    def remove_channel(self, channel_id):
        if channel_id in self.channels:
            self.channels[channel_id].stop()
            del self.channels[channel_id]
            logging.info(f"Channel {channel_id} removed.")
