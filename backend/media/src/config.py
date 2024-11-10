import os

from dotenv import load_dotenv
load_dotenv()

# Streaming: F:\SSAFY\2학기\3. 자율\기타\test
# Playlist : F:\SSAFY\2학기\3. 자율\기타\test\channel_1\playlist
# hls      : F:\SSAFY\2학기\3. 자율\기타\test\channel_1\hls

# 채널 디렉토리 관련
STREAMING_CHANNELS = os.environ.get("STREAMING_CHANNELS")
PLAYLIST_DIR = os.environ.get("PLAYLIST_DIR")
HLS_DIR = os.environ.get("HLS_DIR")

# HLS 프로토콜 관련
SEGMENT_DURATION = 1
SEGMENT_LIST_SIZE = 15
SEGMENT_UPDATE_INTERVAL = 5
SEGMENT_UPDATE_SIZE = 5

# 기본 채널 변수
BASIC_CHANNEL_NAME = 'channel_1'