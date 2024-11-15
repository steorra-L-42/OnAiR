import logging
import json

import sys
import os
sys.path.append(os.path.abspath(os.path.dirname(__file__)))
from story_langchain import chat

class StoryService:
    def __init__(self):
        logging.info("StoryService initialized.")
        pass

    def process(self, value):
        logging.info(f"StoryService process value : {value}")

        chat_result = chat(value['story_title'], value['story_content'], value['channel_info']['personality'], value['channel_info']['tts_engine'])

        volume = 100
        speed_x = 1.0
        tempo = 1.0
        pitch = 0
        last_pitch = 0

        result = {
            "typecast": {
                "text": chat_result['text'],
                "actor": value['channel_info']['tts_engine'],
                "emotion_tone_preset": chat_result['emotion_tone_preset'],
                "volume": volume,
                "speed_x": speed_x,
                "tempo": tempo,
                "pitch": pitch,
                "last_pitch": last_pitch
            },
            "fcm_token" : value['fcm_token'],
            "story_title": value['story_title'],
            "story_id": value['story_id'],
            "story_music": value['story_music'],
        }

        reply = json.dumps(result, ensure_ascii=False)
        return reply