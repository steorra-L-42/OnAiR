import json
import logging

from confluent_kafka import Consumer
from confluent_kafka.admin import AdminClient, NewTopic

import config
import instance
from config import max_story_count
from executor import channel_create_executor
from instance import channel_manager


class KafkaConsumerWrapper:
    def __init__(self, topic, on_message_callback):
        self.topic = topic
        self.on_message_callback = on_message_callback

        # Kafka Consumer 설정
        self.consumer = Consumer({
            'bootstrap.servers': config.bootstrap_server,
            'group.id': config.group_id,
            'auto.offset.reset': config.auto_offset_reset,
            'api.version.request': False,  # 버전 협상 비활성화
        })
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
        return True

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
        if self.create_topic_if_not_exists(dlt_topic):
            logging.info(f"Send Record to {dlt_topic}, Key : {msg.key()}, Value : {msg.value()}")

    def close(self):
        """Consumer 종료"""
        self.consumer.close()
        logging.info("Consumer closed successfully")


def process_channel_creation(msg):
    """Kafka 메시지를 받아 채널을 생성하는 콜백 함수"""

    try:
        value = json.loads(msg.value().decode('utf-8'))
        channel_id = msg.key().decode('utf-8')
        logging.info(f"Received channel creation request for {channel_id}")

        # 채널 생성
        channel_create_executor.submit(channel_manager.add_channel, channel_id, value)
        logging.info(f"Finished to create channel {channel_id}")

    except Exception as e:
        logging.error(f"Error processing message: {e}")
        raise e


def add_channel_info_to_story(msg):
    """Kafka 메시지를 받아 사연에 채널 정보를 더해 Produce하는 콜백 함수"""

    # Kafka 메시지에서 key와 value 추출
    channel_id = msg.key().decode('utf-8')
    value = json.loads(msg.value().decode('utf-8'))

    # 채널이 존재하는지 확인
    if channel_id not in channel_manager.channels:
        logging.warning(f"Channel {channel_id} not found. Skipping.")
        return

    # 채널 정보 가져오기
    channel = channel_manager.channels[channel_id]

    if len(channel.playback_queue.queues["story"]) >= max_story_count:
        logging.warning(f"Queue for story {channel_id} is full. Skipping.")
        return

    # 채널 정보에서 TTS 엔진과 성격(personality) 설정
    tts_engine = channel.tts_engine  # 채널에서 TTS 엔진 정보 가져오기
    personality = channel.personality  # 채널에서 성격 정보 가져오기

    # 채널 정보 추가
    value["channelInfo"] = {
        "ttsEngine": tts_engine,
        "personality": personality
    }

    # 수정된 값 확인 (디버그용)
    logging.info(f"Modified message with channel info: {json.dumps(value, ensure_ascii=False)}")

    # Kafka Producer 설정
    producer = instance.producer

    # 수정된 메시지 생성 및 전송
    value_json = json.dumps(value, ensure_ascii=False).encode('utf-8')
    producer.send_message("story_with_channel_info_topic", channel_id, value_json)
