# /home/fkaus4598/miniconda3/envs/langchain/bin/python /mnt/c/Users/SSAFY/Desktop/ssafy/S11P31D204/backend/langchain/src/kafka_test.py
import instance
import json

producer = instance.producer

def handle_shutdown(signum, frame):
    logging.info(f"Received shutdown signal: {signum}. Shutting down...")
    consumer_manager.close_all_consumers()
    logging.info("All consumers closed successfully.")
    producer.close()
    logging.info("Producer closed successfully.")
    exit(0)

# 사연
def test_story():
    storyTitle = "너무 여유로운 친구"
    storyContent = """
    안녕하세요. 저는 구미 사는 20대 여성입니다. 
    저는 중학교 시절부터 친한 친구가 있습니다.
    저와는 다르게 친구는 느긋하고 무뚝뚝한 성격을 가지고 있습니다.
    평소에는 그런 친구의 성격을 이해하고 지내왔지만, 최근 성격 때문에 다투었습니다.
    저는 여행 계획을 철저하게 세우는 편입니다.
    하지만 친구는 짜여진 일정에 맞춰 움직이는 것을 싫어합니다.
    정성들여 준비한 여행 계획을 친구가 무시하고 싶은 대로 움직이자, 저는 화가 났습니다.
    그 친구가 싫은 것은 아니지만 그런 성격 때문에 답답한 마음이 듭니다.
    어떻게 해야 할까요?
    """

    story_request = {
        "storyTitle" : "{storyTitle}",
        "storyContent" : "{storyContent}",
        "storyId" : "2",
        "storyMusic" : {
            "playListMusicTitle" : "Beautiful Day1",
            "playListMusicArtist" : "U1",
            "playListMusicCoverUrl" : "http://example.com/cover1.jpg"
        },
        "channelInfo" : {
            "ttsEngine": "세나",
            "personality": "tough"
        }
    }

    json_story_request = json.dumps(story_request)
    print(json_story_request)

    producer.send_message('story_with_channel_info_topic', key='channel_1', value=json_story_request.encode('utf-8'))

# 뉴스
def test_news():
    contents_request = {
        "channel_info": {
            "isDefault": "true",
            "ttsEngine": "세나",
            "personality": "kind",
            "newsTopic" : "economic",
        },
        "contentType": "news"
    }
    json_contents_request = json.dumps(contents_request)
    print(json_contents_request)

    producer.send_message('contents_request_topic', key='channel_1', value=json_contents_request.encode('utf-8'))

# 날씨
def test_weather():
    contents_request = {
        "channel_info": {
            "isDefault": "true",
            "ttsEngine": "세나",
            "personality": "kind",
            "newsTopic" : "economic",
        },
        "contentType": "weather"
    }
    json_contents_request = json.dumps(contents_request)
    print(json_contents_request)

    producer.send_message('contents_request_topic', key='channel_1', value=json_contents_request.encode('utf-8'))
while True:
    print('-'*20)
    print('kafka_test.py')
    print("1. story")
    print("2. news")
    print("3. weather")
    print("4. exit")
    print('-'*20)

    input_num = input("input number : ")
    print('-'*20)
    if input_num == '1':
        test_story()
    elif input_num == '2':
        test_news()
    elif input_num == '3':
        test_weather()
    elif input_num == '4':
        break
    else:
        print("wrong input")