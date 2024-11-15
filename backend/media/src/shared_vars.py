
import threading
import concurrent.futures
from config import MAX_WORKERS
######################  공유 변수  ######################

channels = {}
channels_lock = threading.Lock()


# 채널 셋업 스레드 풀
channel_setup_executor = concurrent.futures.ThreadPoolExecutor(max_workers=MAX_WORKERS)
# 채널 데이터 추가 스레드 풀
channel_data_executor = concurrent.futures.ThreadPoolExecutor(max_workers=MAX_WORKERS)