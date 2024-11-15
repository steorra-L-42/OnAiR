# 외부 패키지
import json
import threading

# 내부 패키지
from shared_vars import stream_setup_executor, stream_data_executor
from config import MEDIA_TOPIC, MEDIA_FILE_INFO, MEDIA_IS_START
from src import config
from Stream import Stream
from StreamManager import StreamManager

from logger import log
from kafka_consumer_wrapper import KafkaConsumerWrapper


##################  토픽: media_topic에 대한 consumer 생성  ##################
def create_audio_listener_consumer(stream_manager):
  try:
    log.info(f"컨슈머 생성 [{MEDIA_TOPIC}]")
    consumer = KafkaConsumerWrapper(
      topic=MEDIA_TOPIC,
      on_message_callback=lambda msg: process_input_audio(msg, stream_manager)
    )
    log.info(f"컨슈머 생성 완료 [{MEDIA_TOPIC}]")

    # 스레드를 사용하여 Consumer 메시지 소비 실행
    consumer_thread = threading.Thread(
      target=lambda: consumer.consume_messages(),
      daemon=True
    )
    consumer_thread.start()
    log.info(f"컨슈머 스레드 실행 시작 [{MEDIA_TOPIC}]")
    return consumer

  except Exception as e:
    log.error(f"Failed to create consumer for topic {MEDIA_TOPIC}: {e}")


######################  토픽: media_topic 요청 처리  ######################
def process_input_audio(msg, stream_manager:StreamManager):
  global channels
  # Key, Value 파싱
  key = msg.key().decode('utf-8')
  value = json.loads(msg.value().decode('utf-8'))

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
    future = stream_setup_executor.submit(stream.start_streaming, file_info_list)
    stream.future = future

  # 기존 채널에 음성 추가
  else:
    stream = stream_manager.get_stream(stream_name= key)
    stream_data_executor.submit(stream.add_audio, file_info_list)

######################  토픽: media_topic 요청 처리  ######################
def tmp_get_file_info_list(file_path_list):
  file_info_list = {}
  file_info_list['fileInfo'] = []
  for file_path in file_path_list:
    file_info_list['fileInfo'].append({
      config.MEDIA_FILE_PATH: file_path,
      config.MEDIA_MUSIC_TITLE: '제목없음',
      config.MEDIA_MUSIC_ARTIST: '익명',
      config.MEDIA_TYPE: '없음',
      config.MEDIA_MUSIC_COVER: 'https://marketplace.canva.com/EAExV2m91mg/1/0/100w/canva-%ED%8C%8C%EB%9E%80%EC%83%89-%EB%B0%A4%ED%95%98%EB%8A%98-%EA%B7%B8%EB%A6%BC%EC%9D%98-%EC%95%A8%EB%B2%94%EC%BB%A4%EB%B2%84-QV0Kn6TPPVw.jpg',
    })
  return file_info_list
