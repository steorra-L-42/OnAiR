# 외부 패키지
import json
import threading

from aiofiles import stderr

# 내부 패키지
from shared_vars import stream_setup_executor, stream_data_executor
from config import MEDIA_TOPIC, MEDIA_FILE_INFO, MEDIA_IS_START
from src import config
from Stream import Stream
from StreamManager import StreamManager

from logger import log
from kafka_consumer_wrapper import KafkaConsumerWrapper


##################  토픽에 대한 consumer 생성  ##################
def create_kafka_listener_consumer(topic, callback, stream_manager):
  try:
    log.info(f"컨슈머 생성 [{topic}]")
    consumer = KafkaConsumerWrapper(
      topic=topic,
      on_message_callback=lambda msg: callback(msg, stream_manager)
    )
    log.info(f"컨슈머 생성 완료 [{topic}]")

    # 스레드를 사용하여 Consumer 메시지 소비 실행
    consumer_thread = threading.Thread(
      target=lambda: consumer.consume_messages(),
      daemon=True
    )
    consumer_thread.start()
    log.info(f"컨슈머 스레드 실행 시작 [{topic}]")
    return consumer

  except Exception as e:
    log.error(f"Failed to create consumer for topic {topic}: {e}")




######################  토픽: media_topic 요청 처리  ######################
def process_input_audio(msg, stream_manager:StreamManager):
  # Key, Value 파싱
  key, value = get_key_and_value_from_msg(msg)

  file_info_list = value.get(MEDIA_FILE_INFO, [])
  is_start = value.get(MEDIA_IS_START)
  if isinstance(is_start, str):
    is_start = is_start.lower() == 'true'

  # 새 채널 개설
  if is_start:
    if stream_manager.is_exist(stream_name= key):
      log.error(f"[{key}] 이미 존재하는 채널입니다.")
      return

    stream = Stream(name = key)
    stream_manager.add_stream(stream)
    future = stream_setup_executor.submit(stream.start_streaming, file_info_list, value.get("fcm_token"))
    stream.future = future

  # 기존 채널에 음성 추가
  else:
    if not stream_manager.is_exist(stream_name= key):
      log.error(f"[{key}] 존재하지 않는 채널입니다.")
      return

    stream = stream_manager.get_stream(stream_name= key)
    future = stream_data_executor.submit(stream.add_audio, file_info_list)
    stream.add_future = future




######################  토픽: channel_close_topic 요청 처리  ######################
def process_close_channel(msg, stream_manager:StreamManager):
  # Key, Value 파싱
  stream_name, value = get_key_and_value_from_msg(msg)
  if not stream_manager.is_exist(stream_name):
    return

  log.info(f"[{stream_name}] 채널을 삭제합니다.")
  stream = stream_manager.get_stream(stream_name)
  stream_manager.remove_stream(stream_name)
  stream_setup_executor.submit(stream.stop_streaming_and_remove_stream())




######################  토픽: channel_close_topic 요청 처리  ######################
def get_key_and_value_from_msg(msg):
  key = msg.key().decode('utf-8')
  value = json.loads(msg.value().decode('utf-8'))
  return key, value