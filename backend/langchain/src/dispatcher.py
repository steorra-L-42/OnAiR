import json
import logging

class Dispatcher:
    def __init__(self, story_controller, weather_controller, news_controller):
        self.story_controller = story_controller
        self.weather_controller = weather_controller
        self.news_controller = news_controller
    
    def consume_story_with_channel_info_topic(self, msg):
        # 메시지 처리
        try:
            value = json.loads(msg.value().decode('utf-8'))
            logging.info(f"consume_story_with_channel_info_topic value : {value}")

            channel_id = msg.key().decode('utf-8')
            logging.info(f"Received story with channel info for {channel_id}")

            # TODO : 구현
            # controller로 전달

        except Exception as e:
            logging.error(f"Error processing message: {e}")
            raise e

    def consume_contents_request_topic(msg):
        # 메시지 처리
        try:
            value = json.loads(msg.value().decode('utf-8'))
            logging.info(f"consume_contents_request_topic value : {value}")

            channel_id = msg.key().decode('utf-8')
            logging.info(f"Received contents request for {channel_id}")

            # TODO : 구현
            # contents type에 따라서 다른 처리를 수행
            # weather_controller
            # news_controller

        except Exception as e:
            logging.error(f"Error processing message: {e}")
            raise e