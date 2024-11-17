import logging
import json
import sqlite3

import sys
import os
sys.path.append(os.path.abspath(os.path.dirname(__file__)))
from news_langchain import chat

class NewsService:

    news_topic = {
        'POLITICS' : 1,
        'ECONOMY' : 2,
        'SOCIETY' : 3,
        'LIFE_CULTURE' : 4,
        'WORLD' : 5,
        'IT_SCIENCE' : 6
    }

    base_dir = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
    db_path = os.path.join(base_dir, 'db/news.db')

    def __init__(self):
        logging.info("NewsService instance created.")
        pass

    def process(self, value):
        logging.info(f"NewsService process value : {value}")
        results = []

        news_list = self.get_news_list(topic=value['channel_info']['news_topic'])

        for news in news_list: 

            chat_result = chat(news['title'], news['summary'], value['channel_info']['personality'], value['channel_info']['tts_engine'])

            volume = 200
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
            results.append(result)

        return results

    # DB에서 해당 주제의 뉴스 조회
    def get_news_list(self, topic):
        logging.info(f"NewsService get_news_list topic : {topic}")
        try:
            conn = sqlite3.connect(self.db_path)
            conn.row_factory = sqlite3.Row

            c = conn.cursor()

            # rows = c.execute(f"SELECT * FROM news WHERE topic_id = {self.news_topic[topic]}").fetchall()
            rows = c.execute("SELECT * FROM news WHERE topic_id = (?) LIMIT 5", (self.news_topic[topic],)).fetchall()
            return rows

        except Exception as e:
            logging.error(f"Error cannot get news : {e}")
            raise e