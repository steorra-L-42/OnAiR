import logging
import json

class NewsController:
    def __init__(self, producer, news_service):
        self.producer = producer
        self.news_service = news_service
        logging.info("NewsController initialized.")

    def process(self, channel_id, value):
        logging.info(f"Processing news: {value}")
        results = self.news_service.process(value)
        for result in results:
            reply = json.dumps(result, ensure_ascii=False)
            self.producer.send_message('news_reply_topic', key=channel_id, value=reply.encode('utf-8'))