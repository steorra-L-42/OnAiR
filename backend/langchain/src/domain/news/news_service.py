import logging
import json

class NewsService:
    def __init__(self):
        logging.info("NewsService instance created.")
        pass

    def process(self, value):
        logging.info(f"NewsService process value : {value}")
        results = []

        # // 정치, 경제, 사회, 생활/문화, IT/과학, 세계
        # POLITICS, ECONOMY, SOCIETY, LIFE_CULTURE, IT_SCIENCE, WORLD

        # TODO : 뉴스 별로 llm 모델을 이용하여 응답 생성
        for i in range(3):
            text = """트럼프 이너서클: 트럼프 가족의 정치적 영향력. 
                도널드 트럼프 미국 대통령 당선인의 가족은 정치에서 중요한 역할을 하고 있으며, 특히 장남 도널드 트럼프 주니어가 강력한 영향력을 행사하고 있다. 
                트럼프 주니어는 '마가' 운동을 계승하겠다고 선언하며, 인사와 정치적 결정에 큰 영향을 미치고 있다. 
                둘째 며느리 라라 트럼프도 공화당 전국위원회 공동의장으로 활동하며 정치적 입지를 강화하고 있다. 
                트럼프 가족은 앞으로도 정치에서 중요한 역할을 할 것으로 예상된다."""

            emotion_tone_preset = "normal-1"
            volume = 100
            speed_x = 1.0
            tempo = 1.0
            pitch = 0
            last_pitch = 0

            result = {
                "typecast" : {
	                "text": text,
                    "actor": value['channel_info']['tts_engine'],
                    "emotion_tone_preset": emotion_tone_preset,
                    "volume": volume,
                    "speed_x": speed_x,
                    "tempo": tempo,
                    "pitch": pitch,
                    "last_pitch": last_pitch
	            }
            }
            results.append(result)

        return results