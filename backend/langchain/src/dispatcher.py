import json
import logging

class Dispatcher:
    def __init__(self, story_controller, weather_controller, news_controller):
        self.story_controller = story_controller
        self.weather_controller = weather_controller
        self.news_controller = news_controller
        logging.info("Dispatcher initialized.")
    
    def consume_story_with_channel_info_topic(self, msg):
        # 메시지 처리
        try:
            value = json.loads(msg.value().decode('utf-8'))
            logging.info(f"consume_story_with_channel_info_topic value : {value}")

            channel_id = msg.key().decode('utf-8')
            logging.info(f"Received story with channel info for {channel_id}")

            self.story_controller.process(channel_id, value)

        except Exception as e:
            logging.error(f"Error processing message: {e}")
            raise e

    def consume_contents_request_topic(self, msg):
        # 메시지 처리
        try:
            value = json.loads(msg.value().decode('utf-8'))
            logging.info(f"consume_contents_request_topic value : {value}")

            channel_id = msg.key().decode('utf-8')
            logging.info(f"Received contents request for {channel_id}")

            if(value['contentType'] == 'weather'):
                self.weather_controller.process(channel_id, value)
            elif(value['contentType'] == 'news'):
                self.news_controller.process(channel_id, value)

        except Exception as e:
            logging.error(f"Error processing message: {e}")
            raise e