import logging
import json

class WeatherService:
    def __init__(self):
        logging.info('WeatherService initialized')
        pass

    def process(self, value):
        logging.info(f'WeatherService process value : {value}')

        # TODO : 날씨 정보를 가져와 응답 생성
        text = "2024년 11월 14일 날씨 예보에 따르면, 오늘 오전까지 남부내륙에 짙은 안개가 끼고, 늦은 오후부터 전국 대부분 지역에 가끔 비가 내릴 것으로 예상됩니다. 아침 기온은 서울 12도, 대전 10도, 광주 10도, 대구 8도, 부산 14도이며, 낮 기온은 서울 18도, 대전 19도, 광주 20도, 대구 19도, 부산 21도로 평년보다 높습니다. 특히 낮과 밤의 기온 차가 10도 내외로 크니 건강 관리에 유의해야 합니다. 또한, 짙은 안개로 인해 교통안전에 주의하고, 항공기 운항에 차질이 있을 수 있으니 사전 확인이 필요합니다."

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

        reply = json.dumps(result, ensure_ascii=False)
        return reply
