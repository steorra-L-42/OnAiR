import logging

class NewsController:
    def __init__(self, news_service):
        self.news_service = news_service
        logging.info("NewsController initialized.")