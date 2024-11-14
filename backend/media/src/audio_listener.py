import asyncio
import json
import os.path
# 외부 패키지
import threading

# 내부 패키지
from config import MEDIA_TOPIC
from logger import log

from shared_vars import channels, add_channel
from segmenter import generate_segment, generate_segment_from_files
from kafka_consumer_wrapper import KafkaConsumerWrapper


##################  토픽: media_topic에 대한 consumer 생성  ##################
def create_audio_listener_consumer(loop):
  try:
    log.info(f"Creating consumer for topic: {MEDIA_TOPIC}")
    consumer = KafkaConsumerWrapper(
      topic=MEDIA_TOPIC,
      on_message_callback=lambda msg: process_input_audio(msg, loop)
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
def process_input_audio(msg, loop):
  global channels
  key = msg.key().decode('utf-8')
  value = json.loads(msg.value().decode('utf-8'))

  # file_info_list = value.get("fileInfo", [])
  file_info_list = tmp_get_file_info_list(value.get("filePath"))
  is_start = value.get("isStart", False)

  if is_start:
    add_channel(
      channel_name = key,
      file_info_list = file_info_list.get("fileInfo"),
      loop=loop
    )
  else:
    channel = channels[key]
    now_index = channel['queue'].last_index

    with channel['queue'].lock:
      # 'queue'에 메타데이터 저장
      channel['queue'].add_metadata_all(file_info_list.get("fileInfo"))
      
      # 세그먼트 파일 생성
      channel['queue'].last_index = generate_segment_from_files(
        channel['hls_path'],            # 세그먼트 생성할 경로
        file_info_list.get("fileInfo"), # 세그먼트를 생성할 파일
        now_index                       # 시작 인덱스 번호
      )
    
    # 'queue'에 세그먼트 파일 저장
    channel['queue'].init_segments_from_directory(
      channel['hls_path'],              # 세그먼트를 가져올 경로
      now_index,                        # 등록할 세그먼트 파일의 인덱스 범위(시작)
      channel['queue'].last_index       # 등록할 세그먼트 파일의 인덱스 범위(끝)
    )


######################  토픽: media_topic 요청 처리  ######################
def tmp_get_file_info_list(file_path_list):
  file_info_list = {}
  file_info_list['fileInfo'] = []
  for file_path in file_path_list:
    file_info_list['fileInfo'].append({
      'filePath': file_path,
      'fileTitle': '제목',
      'fileAuthor': '가수',
      'fileGenre': '장르',
      'fileCover': 'https://marketplace.canva.com/EAExV2m91mg/1/0/100w/canva-%ED%8C%8C%EB%9E%80%EC%83%89-%EB%B0%A4%ED%95%98%EB%8A%98-%EA%B7%B8%EB%A6%BC%EC%9D%98-%EC%95%A8%EB%B2%94%EC%BB%A4%EB%B2%84-QV0Kn6TPPVw.jpg',
    })
  return file_info_list
