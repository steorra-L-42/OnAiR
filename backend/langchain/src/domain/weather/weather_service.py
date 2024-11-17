import logging
import json
import sqlite3

import sys
import os
sys.path.append(os.path.abspath(os.path.dirname(__file__)))
from weather_langchain import chat

class WeatherService:

    base_dir = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
    db_path = os.path.join(base_dir, 'db/weather.db')

    def __init__(self):
        logging.info('WeatherService initialized')
        pass

    def process(self, value):
        logging.info(f'WeatherService process value : {value}')

        weather = self.get_weather()
        chat_result = chat(weather, value['channel_info']['personality'])

        volume = 150
        speed_x = 1.0
        tempo = 1.0
        pitch = 0
        last_pitch = 0

        result = {
            "typecast" : {
	            "text": chat_result['text'],
                "actor": value['channel_info']['tts_engine'],
                "emotion_tone_preset": chat_result['emotion_tone_preset'],
                "volume": volume,
                "speed_x": speed_x,
                "tempo": tempo,
                "pitch": pitch,
                "last_pitch": last_pitch
	        }
        }

        reply = json.dumps(result, ensure_ascii=False)
        return reply

    def get_weather(self):
        logging.info('WeatherService get_weather')

        try:
            conn = sqlite3.connect(self.db_path)
            conn.row_factory = sqlite3.Row

            c = conn.cursor()
            row = c.execute("SELECT * FROM weather ORDER BY created_at DESC LIMIT 1").fetchone()
            return row['content']

        except Exception as e:
            logging.error(f'Error cannot get weather : {e}')
            raise e