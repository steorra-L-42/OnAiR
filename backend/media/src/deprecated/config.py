# from dotenv import load_dotenv
# import os
#
# load_dotenv()
#
# ### 미디어 서버 디렉토리 경로 변수 ###
# ROOT_PATH = os.environ.get('ROOT_DIR')
# STREAMING_CHANNEL_PATH = os.environ.get('STREAMING_CHANNEL_PATH')
# CHANNEL_PATHS = [os.path.join(STREAMING_CHANNEL_PATH, f"channel_{i}") for i in range(4)]
#
# HLS_OUTPUT_DIR = "hls_output"
# SOURCES_DIR = "sources"
#
# LOG_FILES_PATH = os.environ.get('LOG_FILES_PATH')
#
# ### HLS 프로토콜 설정 변수 정의 ###
# HLS_TIME = 2
# HLS_LIST_SIZE = 5
# HLS_DELETE_THRESHOLD = 360
#
# ### 기본 채널 설정 ###
# BASIC_CHANNEL_NO = 1
# BASIC_CHANNEL_NAMES = [f"channel_{i}" for i in range(4)]
# BASIC_CHANNEL_INIT_MUSIC = os.getenv('BASIC_CHANNEL_INIT_MUSIC', 'False').lower() == 'true'
#
#
# ### 모든 채널 설정 ###
# CHANNEL_RESET = os.getenv('CHANNEL_RESET', 'False').lower() == 'true'
#
# ### 기타 ###
# SERVER_BASE_URL = os.environ.get('SERVER_BASE_URL')
# CLIENT_BASE_URL = os.environ.get('CLIENT_BASE_URL')