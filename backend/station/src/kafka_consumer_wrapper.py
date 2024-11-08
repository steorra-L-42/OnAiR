from confluent_kafka import Consumer
import config


class kafka_consumer_wrapper:
    def __init__(self):
        # Kafka 설정을 기반으로 Consumer 객체 생성
        self.consumer = Consumer({
            'bootstrap.servers': config.bootstrap_server,
            'group.id': config.group_id,
            'auto.offset.reset': config.auto_offset_reset
        })
