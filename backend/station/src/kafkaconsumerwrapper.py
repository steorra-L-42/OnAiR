import logging

from confluent_kafka import Consumer, Producer
from confluent_kafka.admin import AdminClient, NewTopic

import config


class KafkaConsumerWrapper:
    def __init__(self, topic, on_message_callback):
        self.topic = topic
        self.on_message_callback = on_message_callback

        # Kafka Consumer 설정
        self.consumer = Consumer({
            'bootstrap.servers': config.bootstrap_server,
            'group.id': config.group_id,
            'auto.offset.reset': config.auto_offset_reset,
        })
        self.producer = Producer({'bootstrap.servers': config.bootstrap_server})
        self.admin_client = AdminClient({'bootstrap.servers': config.bootstrap_server})

    def create_topic_if_not_exists(self, topic_name):
        """토픽이 존재하지 않으면 생성"""
        existing_topics = self.admin_client.list_topics(timeout=5).topics
        if topic_name not in existing_topics:
            new_topic = NewTopic(topic_name, num_partitions=1, replication_factor=1)
            self.admin_client.create_topics([new_topic])
            logging.info(f"Topic '{topic_name}' created.")
        else:
            logging.info(f"Topic '{topic_name}' already exists.")

    def consume_messages(self):
        """메시지 소비 및 처리"""
        self.consumer.subscribe([self.topic])
        logging.info(f"Subscribed to topic: {self.topic}")

        while True:
            msg = None
            try:
                # poll 단계에서 메시지 소비
                msg = self.consumer.poll(1.0)
                if msg is None:
                    continue

                # poll 단계에서 에러 처리
                if msg.error():
                    logging.error(f"Consumer error: {msg.error().str()}")
                    self.send_to_dlt(msg)
                    continue

                # 정상 메시지 처리
                self.process_message(msg)

            except Exception as e:
                logging.error(f"Unhandled error during polling: {e}")
                if msg:
                    self.send_to_dlt(msg)

    def process_message(self, msg):
        """메시지 처리 및 오류 시 DLT 전송"""
        try:
            logging.info(f"Processing message: {msg.value().decode('utf-8')}")
            self.on_message_callback(msg)
            self.consumer.commit(msg)
        except Exception as e:
            logging.error(f"Error in callback: {e}")
            self.send_to_dlt(msg)

    def send_to_dlt(self, msg):
        """DLT로 메시지 전송"""
        dlt_topic = f"{self.topic}-dlt"

        # DLT 토픽이 없으면 생성
        self.create_topic_if_not_exists(dlt_topic)

        self.producer.produce(
            dlt_topic,
            key=msg.key(),
            value=msg.value(),
            headers=msg.headers()
        )
        self.producer.flush()
        logging.error(f"Message sent to DLT: {msg.value().decode('utf-8')}")

    def close(self):
        """Consumer 종료"""
        self.consumer.close()
        logging.info("Consumer closed successfully")
