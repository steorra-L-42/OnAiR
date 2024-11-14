import logging

class StoryController:
    def __init__(self, story_service):
        self.story_service = story_service
        logging.info("StoryController initialized.")