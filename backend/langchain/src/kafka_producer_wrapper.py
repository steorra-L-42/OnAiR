import logging
import threading

from confluent_kafka import Producer

import config

class KafkaProducerWrapper:
    def __init__(self):
        self.producer = Producer({"bootstrap.servers": config.bootstrap_server})
        self.lock = threading.Lock()
        logging.info("ProducerWrapper initialized.")

    def send_message(self, topic, key, value, headers=None):
        try:
            self.producer.produce(
                topic=topic,
                key=key,
                value=value,
                headers=headers,
            )
            self.producer.flush()
            logging.info(f"Message sent to {topic}: {value.decode('utf-8')}")
        except Exception as e:
            logging.error(f"Error in sending message: {e}")
    
    def close(self):
        # producer 종료
        logging.info("ProducerWrapper closed successfully.")
