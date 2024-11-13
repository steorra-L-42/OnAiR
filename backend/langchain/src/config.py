import os

from dotenv import load_dotenv, find_dotenv

# .env 파일 로드
dotenv_path = find_dotenv()
if dotenv_path:
    load_dotenv(dotenv_path)

# 환경 변수에서 Kafka 설정 가져오기
bootstrap_server = os.getenv("BOOTSTRAP_SERVERS")
group_id = os.getenv("LANGCHAIN_CONSUMER_GROUP_ID")
auto_offset_reset = os.getenv("AUTO_OFFSET_RESET")