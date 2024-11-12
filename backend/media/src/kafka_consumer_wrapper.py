import logging
from collections import deque

from confluent_kafka import Consumer
from confluent_kafka.admin import AdminClient, NewTopic

from config import BOOTSTRAP_SERVERS, MEDIA_CONSUMER_GROUP_ID, AUTO_OFFSET_RESET
dlt_queue = deque()  # DLT 전송을 위한 큐 생성


class KafkaConsumerWrapper:
  def __init__(self, topic, on_message_callback):
    self.topic = topic
    self.on_message_callback = on_message_callback

    # Kafka Consumer 설정
    self.consumer = Consumer({
      'bootstrap.servers': BOOTSTRAP_SERVERS,
      'group.id': MEDIA_CONSUMER_GROUP_ID,
      'auto.offset.reset': AUTO_OFFSET_RESET,
      'api.version.request': False,  # 버전 협상 비활성화
    })
    self.admin_client = AdminClient({'bootstrap.servers': BOOTSTRAP_SERVERS})

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