from channel_manager import ChannelManager
from kafka_producer_wrapper import KafkaProducerWrapper

producer = KafkaProducerWrapper()
channel_manager = ChannelManager(producer)
