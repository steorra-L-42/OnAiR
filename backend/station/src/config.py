from dotenv import load_dotenv
import os

# .env 파일 로드
load_dotenv()

# 환경 변수에서 Kafka 설정 가져오기
bootstrap_server = os.getenv("BOOTSTRAP_SERVER")
group_id = os.getenv("CONSUMER_GROUP_ID")
auto_offset_reset = os.getenv("AUTO_OFFSET_RESET")