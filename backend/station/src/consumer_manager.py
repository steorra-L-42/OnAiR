import logging
import threading

from src.content_provider import handle_weather, handle_news, handle_story
from src.kafka_consumer_wrapper import KafkaConsumerWrapper, process_channel_creation, add_channel_info_to_story

# 현재 실행 중인 모든 Consumer 인스턴스를 저장할 딕셔너리
consumers = {}


def create_consumer(topic, callback):
    """새로운 Kafka Consumer를 생성하고 딕셔너리에 추가"""
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
    """모든 필요한 Kafka Consumer를 생성"""
    logging.info("Initializing all consumers...")

    # 채널 생성 토픽
    create_consumer('channel_info_topic', process_channel_creation)

    # 콘텐츠 제공 관련 토픽
    create_consumer('weather_reply_topic', handle_weather)
    create_consumer('news_reply_topic', handle_news)
    create_consumer('story_reply_topic', handle_story)
    create_consumer('story_topic', add_channel_info_to_story)

    logging.info("All consumers initialized successfully.")


def close_all_consumers():
    """모든 Consumer를 안전하게 종료"""
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
