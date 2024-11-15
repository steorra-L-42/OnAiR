import os
from dotenv import load_dotenv, find_dotenv

dotenv_path = find_dotenv()
if os.path.exists(dotenv_path):
  load_dotenv(dotenv_path)

# 채널 디렉토리 관련
STREAMING_CHANNELS = os.environ.get("STREAMING_CHANNELS")
PLAYLIST_DIR = os.environ.get("PLAYLIST_DIR")
HLS_DIR = os.environ.get("HLS_DIR")

# HLS 프로토콜 관련
SEGMENT_DURATION = 4
SEGMENT_LIST_SIZE = 10
SEGMENT_UPDATE_INTERVAL = 4
SEGMENT_UPDATE_SIZE = 1

SEGMENT_FILE_INDEX_START=8
SEGMENT_FILE_INDEX_END=14

# 카프카
BOOTSTRAP_SERVERS=os.environ.get('BOOTSTRAP_SERVERS')
MEDIA_CONSUMER_GROUP_ID=os.environ.get('MEDIA_CONSUMER_GROUP_ID')
AUTO_OFFSET_RESET=os.environ.get('AUTO_OFFSET_RESET')
MEDIA_TOPIC='media_topic'

# 카프카 레코드
MEDIA_FILE_INFO = "file_info"
MEDIA_FILE_PATH = "file_path"
MEDIA_TYPE = "type"

MEDIA_MUSIC_TITLE = "music_title"
MEDIA_MUSIC_ARTIST = "music_artist"
MEDIA_MUSIC_COVER = "music_cover_url"

MEDIA_IS_START = "is_start"
MEDIA_FCM_TOKEN = "fcm_token"

# 기본 채널 변수
BASIC_CHANNEL_NAME = 'channel_1'

# 스레드 풀
MAX_WORKERS = 3