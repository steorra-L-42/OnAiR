import logging
import json

class NewsService:
    def __init__(self):
        logging.info("NewsService instance created.")
        pass

    def process(self, value):
        logging.info(f"NewsService process value : {value}")
        results = []
        # TODO : 뉴스 별로 llm 모델을 이용하여 응답 생성
        # replys = json.dumps(results)
        # return replys