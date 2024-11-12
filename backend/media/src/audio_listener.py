import os.path
# 외부 패키지
import threading

# 내부 패키지
from config import MEDIA_TOPIC
from logger import log

from shared_vars import channels
from segmenter import generate_segment
from kafka_consumer_wrapper import KafkaConsumerWrapper


##################  토픽: media_topic에 대한 consumer 생성  ##################
def create_audio_listener_consumer():
  try:
    log.info(f"Creating consumer for topic: {MEDIA_TOPIC}")
    consumer = KafkaConsumerWrapper(
      topic=MEDIA_TOPIC,
      on_message_callback=process_input_audio
    )
    log.info(f"Consumer for topic {MEDIA_TOPIC} created successfully.")

    # 스레드를 사용하여 Consumer 메시지 소비 실행
    consumer_thread = threading.Thread(
      target=lambda: consumer.consume_messages(),
      daemon=True
    )
    consumer_thread.start()
    log.info(f"Consumer thread for topic {MEDIA_TOPIC} started.")
    return consumer

  except Exception as e:
    log.error(f"Failed to create consumer for topic {MEDIA_TOPIC}: {e}")


######################  토픽: media_topic 요청 처리  ######################
def process_input_audio(msg):
  global channels
  key = msg.key().decode('utf-8')
  new_file_path:str = msg.value().decode('utf-8')
  channel = channels[key]

  log.info(f'key [{key}]')
  log.info(f'val [{new_file_path}]')

  if channel == None:
    log.error(f'잘못된 채널 이름입니다 [{key}]')
    return
  if not os.path.isfile(new_file_path):
    log.error(f"파일이 존재하지 않거나 잘못된 경로입니다 [{new_file_path}]")
    return False
  if not new_file_path.lower().endswith('.mp3'):
    log.error(f"잘못된 파일 형식입니다 [{new_file_path}]")
    return False

  log.info(f'mp3 파일 생성 [{channel["queue"].last_index}]')
  log.info(f'mp3 파일 생성 [{new_file_path}]')

  channel['queue'].last_index = generate_segment(
    channel['hls_path'],            # 세그먼트 생성할 경로
    new_file_path,                  # 세그먼트 생성할 파일
    channel['queue'].last_index     # index
  )
  channel['queue'].init_segments_from_directory(
    channel['hls_path'],            # 세그먼트를 가져올 경로
    channel['queue'].last_index-1   # 세그먼트 파일의 인덱스
  )