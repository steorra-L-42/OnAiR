import logging
import json

class WeatherService:
    def __init__(self):
        logging.info('WeatherService initialized')
        pass

    def process(self, value):
        logging.info(f'WeatherService process value : {value}')
        # TODO : 날씨 정보를 가져와 응답 생성
        # reply = json.dumps(result)
        # return reply