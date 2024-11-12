import os

from dotenv import load_dotenv, find_dotenv

# .env 파일 로드
dotenv_path = find_dotenv()
if dotenv_path:
    print(dotenv_path)
    load_dotenv(dotenv_path)

# 환경 변수에서 Kafka 설정 가져오기
bootstrap_server = os.getenv("BOOTSTRAP_SERVER")
group_id = os.getenv("CONSUMER_GROUP_ID")
auto_offset_reset = os.getenv("AUTO_OFFSET_RESET")

# typecast.ai API_KEY
sena_token = os.getenv("SENA_TOKEN")
jerome_token = os.getenv("JEROME_TOKEN")
hyeonji_token = os.getenv("HYEONJI_TOKEN")
eunbin_token = os.getenv("EUNBIN_TOKEN")

# typecast.ai ACTOR_ID
sena_actor_id = os.getenv("SENA_ACTOR_ID")
jerome_actor_id = os.getenv("JEROME_ACTOR_ID")
hyeonji_actor_id = os.getenv("HYEONJI_ACTOR_ID")
eunbin_actor_id = os.getenv("EUNBIN_ACTOR_ID")
