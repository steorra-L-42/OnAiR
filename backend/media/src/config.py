import os
from dotenv import load_dotenv
load_dotenv()

# Directory paths
STREAMING_CHANNELS = os.environ.get("STREAMING_CHANNELS")
PLAYLIST_DIR = os.environ.get("PLAYLIST_DIR")
HLS_DIR = os.environ.get("HLS_DIR")

# HLS protocol settings
SEGMENT_DURATION = 2  # Duration of each segment in seconds
SEGMENT_LIST_SIZE = 5  # Number of segments in the playlist

# Basic channel name
BASIC_CHANNEL_NAME = 'channel_1'
