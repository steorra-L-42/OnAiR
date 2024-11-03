import os

# 디렉토리 관련 변수 정의
ROOT_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__), '..'))
STREAMING_CH_DIR = os.path.join(ROOT_DIR, "streaming_channels")
MEDIA_SEGMENTS = "media_segments"
MEDIA_SOURCES = "media_sources"

ch_dirs = [os.path.join(STREAMING_CH_DIR, f"channel{i}") for i in range(4)]
m3u8_dirs = [os.path.join(ch_dirs[i], "media_segments/index.m3u8") for i in range(4)]