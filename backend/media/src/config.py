import os

from dotenv import load_dotenv
load_dotenv()

# 음성파일 디렉토리
PLAYLIST_PATH = os.environ.get("PLAYLIST_PATH")
EMPTY_MP3 = os.environ.get("EMPTY_MP3")

# 스트리밍 변수
CHUNK_SIZE = 4096
CHUNK_STEP = 12

# 기본 채널 변수
BASIC_CHANNEL_NAME = 'channel_1'