import logging


class DJ:
    def __init__(self, channel, playback_queue):
        self.channel = channel
        self.playback_queue = playback_queue

    def stop(self):
        """DJ 종료"""
        logging.info(f"Stopping DJ for channel {self.channel.channel_id}")
