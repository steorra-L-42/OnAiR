import logging
import threading

from src.kafka.kafka_consumer_wrapper import KafkaConsumerWrapper
import src.instance as instance

# Consumer 생성 및 삭제
consumers = {}

def create_consumer(topic, callback):
    # 새로운 Consumer 생성 후 consumers 딕셔너리에 추가
    try:
        logging.info(f"Creating consumer for topic: {topic}")
        consumer = KafkaConsumerWrapper(
            topic=topic,
            on_message_callback=callback
        )
        consumers[topic] = consumer
        logging.info(f"Consumer for topic {topic} created successfully.")

        # 스레드를 사용하여 Consumer 메시지 소비 실행
        consumer_thread = threading.Thread(
            target=lambda: consumer.consume_messages(),
            daemon=True
        )
        consumer_thread.start()
        logging.info(f"Consumer thread for topic {topic} started.")

    except Exception as e:
        logging.error(f"Failed to create consumer for topic {topic}: {e}")


def create_consumers():
    # 모든 Consumer 생성
    logging.info("Initializing all consumers...")

    # Consumer 생성
    dispatcher = instance.dispatcher
    create_consumer('story_with_channel_info_topic', dispatcher.consume_story_with_channel_info_topic)
    create_consumer('contents_request_topic', dispatcher.consume_contents_request_topic)

    logging.info("All consumers initialized successfully.")

def close_all_consumers():
   # 모든 Consumer 종료
    logging.info("Shutting down all consumers...")

    for topic, consumer in consumers.items():
        try:
            consumer.close()
            logging.info(f"Consumer for topic '{topic}' closed successfully.")
        except Exception as e:
            logging.error(f"Error closing consumer for topic '{topic}': {e}")

    # Consumer 딕셔너리 비우기
    consumers.clear()
    logging.info("All consumers have been shut down.")