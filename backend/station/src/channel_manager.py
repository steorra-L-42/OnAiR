import json
import logging

from confluent_kafka import KafkaError

from src.channel import Channel
from src.kafka_consumer_wrapper import kafka_consumer_wrapper


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


def listen_for_channel_creation():
    # Kafka Consumer 설정
    consumer = kafka_consumer_wrapper().consumer
    consumer.subscribe(['channel_info_topic'])

    channel_manager = ChannelManager()

    try:
        while True:
            msg = consumer.poll(1.0)
            if msg is None:
                continue

            if msg.error():
                if msg.error().code() != KafkaError._PARTITION_EOF:
                    logging.error(f"Error occurred: {msg.error().str()}")
                    continue
                logging.warning(f"End of partition reached {msg.topic()} / {msg.partition()}")

            # 메시지 수신 시 채널 생성 로직 수행
            value = json.loads(msg.value().decode('utf-8'))
            channel_id = msg.key().decode('utf-8')

            logging.info(f"Received channel creation request for {channel_id}")
            # 채널 생성
            channel_manager.add_channel(channel_id, value)
    except KeyboardInterrupt:
        pass
    finally:
        consumer.close()
