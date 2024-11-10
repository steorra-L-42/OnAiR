import json
import logging

from src.channel import Channel
from src.kafkaconsumerwrapper import KafkaConsumerWrapper


class ChannelManager:
    def __init__(self):
        self.channels = {}

    def add_channel(self, channel_id, config):
        if channel_id not in self.channels:
            # 채널 생성
            self.channels[channel_id] = Channel(channel_id, config)
            logging.info(f"Channel {channel_id} created.")

        else:
            # 채널_id 충돌.
            logging.warning(f"Channel {channel_id} already exists.")

    def remove_channel(self, channel_id):
        if channel_id in self.channels:
            self.channels[channel_id].stop()
            del self.channels[channel_id]
            logging.info(f"Channel {channel_id} removed.")


def process_channel_creation(msg):
    """Kafka 메시지를 받아 채널을 생성하는 콜백 함수"""

    channel_manager = ChannelManager()
    try:
        value = json.loads(msg.value().decode('utf-8'))
        channel_id = msg.key().decode('utf-8')
        logging.info(f"Received channel creation request for {channel_id}")

        # 채널 생성
        channel_manager.add_channel(channel_id, value)
    except Exception as e:
        logging.error(f"Error processing message: {e}")
        raise e


def listen_for_channel_creation():
    """Kafka Consumer를 사용하여 채널 생성 요청을 수신"""
    consumer = KafkaConsumerWrapper(
        topic='channel_info_topic',
        on_message_callback=process_channel_creation
    )

    try:
        consumer.consume_messages()
    except KeyboardInterrupt:
        logging.info("Consumer stopped by user.")
    finally:
        consumer.close()
