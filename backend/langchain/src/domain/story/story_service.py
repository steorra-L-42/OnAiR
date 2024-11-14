import logging
import json

class StoryService:
    def __init__(self):
        logging.info("StoryService initialized.")
        pass

    def process(self, value):
        logging.info(f"StoryService process value : {value}")

        # TODO : 사연에 대한 응답 생성
        text = """좋아요, 청취자님의 사연을 먼저 읽어드릴게요.
구미에서 온 20대 여성 청취자님의 고민 가득한 사연을 들여드리겠습니다.
안녕하세요. 저는 구미 사는 20대 여성입니다. 저에게는 중학교 시절부터 친한 친구가 있습니다. 
저와는 다르게 친구는 느긋하고 무뚝뚝한 성격을 가지고 있습니다. 
평소에는 그런 친구의 성격을 이해하고 지내왔지만, 최근에는 그 성격 때문에 다투게 되었습니다.
저는 여행 계획을 철저하게 세우는 편이라 미리 여행 일정을 정해놓고 갔습니다. 
하지만 친구는 짜여진 일정에 맞춰 움직이는 것을 싫어합니다. 
정성 들여 준비한 여행 계획을 친구가 무시하고 싶은 대로 움직이자, 저는 화가 났습니다. 
그 친구가 싫은 것은 아니지만, 그런 성격 때문에 답답한 마음이 듭니다.
어떻게 해야 할까요?
사연을 들으니 마음이 참 복잡하셨을 것 같아요. 
서로 다른 성향 때문에 충돌이 생기면 더 속상하게 느껴지죠. 
특히 애써 준비한 여행 계획을 친구가 다르게 받아들일 때, 청취자님의 정성이 무시된 것 같아 아쉬운 마음이 드셨을 거예요.
계획적이고 꼼꼼한 성격의 청취자님께는 여행의 일정이 중요하고, 친구분은 자유롭게 움직이는 걸 더 선호하시니 서로를 이해하는 게 쉽지 않으셨을 거라 생각해요. 
청취자님의 진심이 잘 전달될 수 있도록 작은 대화를 시도해 보는 건 어떨까요? 
그러면 친구분도 청취자님의 마음을 이해하고, 다음엔 서로 조금씩 맞춰가며 즐거운 여행을 함께할 수 있을 거예요."""
        
        emotion_tone_preset = "normal-1"
        volume = 100
        speed_x = 1.0
        tempo = 1.0
        pitch = 0
        last_pitch = 0

        result = {
            "typecast": {
                "text": text,
                "actor": value['channelInfo']['ttsEngine'],
                "emotion_tone_preset": emotion_tone_preset,
                "volume": volume,
                "speed_x": speed_x,
                "tempo": tempo,
                "pitch": pitch,
                "last_pitch": last_pitch
            },
            "storyId": value['storyId'],
            "storyMusic": value['storyMusic']
        }

        reply = json.dumps(result, ensure_ascii=False)
        return reply