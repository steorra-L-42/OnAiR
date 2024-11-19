

import concurrent.futures
from config import *
from StreamManager import StreamManager

######################  공유 변수  ######################

stream_manager = StreamManager()


# 채널 셋업 스레드 풀
stream_setup_executor = concurrent.futures.ThreadPoolExecutor(max_workers= MAX_WORKERS)
# 채널 데이터 추가 스레드 풀
stream_data_executor = concurrent.futures.ThreadPoolExecutor(max_workers= MAX_WORKERS)