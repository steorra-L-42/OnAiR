import logging

class StoryController:
    def __init__(self, producer, story_service):
        self.producer = producer
        self.story_service = story_service
        logging.info("StoryController initialized.")

    def process(self, channel_id, value):
        logging.info(f"Processing story: {value}")
        reply = self.story_service.process(value)
        self.producer.send_message('story_reply_topic', key=channel_id, value=reply.encode('utf-8'))