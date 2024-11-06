import os


# 디렉토리 관련 변수 정의
ROOT_DIR = '/'
# ROOT_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__), '..'))
STREAMING_CH_DIR = os.path.join(ROOT_DIR, "streaming_channels")
ch_path = [os.path.join(STREAMING_CH_DIR, f"channel_{i}") for i in range(4)]

hls_output_dir = "hls_output"
sources_dir = "sources"


# url
base_url = 'http://localhost:8000'


# HLS 프로토콜 설정 변수 정의
HLS_TIME = 2
HLS_LIST_SIZE = 5
HLS_DELETE_THRESHOLD = 360


# 기본 채널 설정
BASIC_CHANNLE_STREAM_NAME = "channel_1"
INIT_MUSIC_ON_STARTUP = False
RESET = True