import os

from dotenv import load_dotenv, find_dotenv

# .env 파일 로드
dotenv_path = find_dotenv()
if dotenv_path:
    load_dotenv(dotenv_path)

# 환경 변수에서 Kafka 설정 가져오기
BOOTSTRAP_SERVER = os.getenv("BOOTSTRAP_SERVERS")
GROUP_ID = os.getenv("LANGCHAIN_CONSUMER_GROUP_ID")
AUTO_OFFSET_RESET = os.getenv("AUTO_OFFSET_RESET")

import logging

LOG_LEVEL = logging.WARNING
if(os.getenv("LOG_LEVEL") == "DEBUG"):
    LOG_LEVEL = logging.DEBUG
elif(os.getenv("LOG_LEVEL") == "INFO"):
    LOG_LEVEL = logging.INFO
elif(os.getenv("LOG_LEVEL") == "WARN"):
    LOG_LEVEL = logging.WARNING
elif(os.getenv("LOG_LEVEL") == "ERROR"):
    LOG_LEVEL = logging.ERROR