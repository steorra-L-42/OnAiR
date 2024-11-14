import logging

class WeatherController:
    def __init__(self, producer, weather_service):
        self.producer = producer
        self.weather_service = weather_service
        logging.info('WeatherController initialized')

    def process(self, channel_id, value):
        logging.info(f'Processing weather: {value}')
        self.weather_service.process(value)
        reply = self.weather_service.process(value)
        self.producer.send_message('weather_reply_topic', key=channel_id, value=reply.encode('utf-8'))