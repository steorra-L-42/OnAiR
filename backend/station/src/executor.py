from concurrent.futures import ThreadPoolExecutor

from config import max_channels

channel_create_executor = ThreadPoolExecutor(max_workers=max_channels)
