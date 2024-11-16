# 외부 패키지
import threading

# 내부 패키지
import Stream
from logger import log

class StreamManager:
    def __init__(self):
        self.streams = {}
        self.lock = threading.Lock()

    def add_stream(self, stream: Stream):
        with self.lock:
            self.streams[stream.name] = stream

    def remove_stream(self, stream_name):
        with self.lock:
            del self.streams[stream_name]

    def get_stream(self, stream_name):
        return self.streams[stream_name]

    def get_all_stream(self):
        return [stream for stream in self.streams.values()]

    def remove_stream_all(self):
        self.streams.clear()

    def is_exist(self, stream_name):
        return (stream_name in self.streams)