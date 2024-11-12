
# 외부 패키지
import os
import time
from watchdog.observers import Observer
from watchdog.events import FileSystemEventHandler

# 내부 패키지
from segmenter import generate_segment
from logger import log

class PlaylistHandler(FileSystemEventHandler):
  def __init__(self, channel):
    self.channel = channel

  def on_created(self, event):
    log.info(f'mp3 파일 생성 [{self.channel["queue"].last_index}]')
    log.info(f'mp3 파일 생성 [{event.src_path}]')
    if not event.is_directory:
      self.channel['queue'].last_index = generate_segment(
          self.channel['hls_path'],          # 세그먼트 생성할 경로
          event.src_path,                    # 세그먼트 생성할 파일
          self.channel['queue'].last_index   # index
      )
      self.channel['queue'].init_segments_from_directory(
          self.channel['hls_path'],          # 세그먼트를 가져올 경로
          self.channel['queue'].last_index-1 # 세그먼트 파일의 인덱스
      )


def listen_directory(channel):
  directory_event_handler = PlaylistHandler(channel)
  observer = Observer()
  observer.schedule(directory_event_handler, channel['playlist_path'], recursive=False)
  observer.start()
  return observer
