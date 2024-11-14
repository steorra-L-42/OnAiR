import logging
import json

class StoryService:
    def __init__(self):
        logging.info("StoryService initialized.")
        pass

    def process(self, value):
        logging.info(f"StoryService process value : {value}")
        # TODO : 사연에 대한 응답 생성
        # reply = json.dumps(result)
        # return reply