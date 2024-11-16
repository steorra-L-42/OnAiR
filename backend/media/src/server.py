# 외부 패키지
from contextlib import asynccontextmanager
from fastapi import FastAPI
from fastapi.responses import JSONResponse, FileResponse
from fastapi.exceptions import HTTPException
from fastapi.middleware.cors import CORSMiddleware
import os

from intervaltree import IntervalTree

# 내부 패키지
from config import STREAMING_CHANNELS, HLS_DIR, \
  SEGMENT_FILE_INDEX_END, SEGMENT_FILE_INDEX_START, MEDIA_TYPE, \
  MEDIA_MUSIC_TITLE, MEDIA_MUSIC_ARTIST, MEDIA_MUSIC_COVER, CHANNEL_CLOSE_TOPIC, MEDIA_TOPIC
from shared_vars import stream_setup_executor, stream_data_executor, stream_manager

from logger import log
from kafka_listener import create_kafka_listener_consumer, process_input_audio, process_close_channel

app = FastAPI()


######################  CORS 설정  ######################
app.add_middleware(
  CORSMiddleware,
  allow_origins=["*"],
  allow_credentials=True,
  allow_methods=["*"],
  allow_headers=["*"],
)


######################  서버 INIT  ######################
@asynccontextmanager
async def lifespan(app: FastAPI):
  global stream_manager
  log.info("미디어 서버 초기화 루틴 시작")
  media_consumer = create_kafka_listener_consumer(MEDIA_TOPIC, process_input_audio, stream_manager)
  close_consumer = create_kafka_listener_consumer(CHANNEL_CLOSE_TOPIC, process_close_channel, stream_manager)
  log.info("미디어 서버 가동")

  yield
  log.info(f"서버 종료 루틴 시작")
  try:
    for stream in stream_manager.get_all_stream():
      stream.stop_streaming_and_remove_stream()
    stream_manager.remove_stream_all()
    log.info(f"스트림 정리 완료")
  except Exception as e:
    log.error(f"스트림 정리 에러 발생 [{e}]")

  try:
    stream_setup_executor.shutdown(False)
    stream_data_executor.shutdown(False)
    log.info(f"ThreadPool 해제 완료")
  except Exception as e:
    log.error(f"ThreadPool 해제 에러 발생 [{e}]")

  try:
    media_consumer.stop_event.set()
    media_consumer.close()
    close_consumer.stop_event.set()
    close_consumer.close()
    log.info("카프카 컨슈머 종료")
  except Exception as e:
    log.info(f"카프카 컨슈머 종료 에러 발생 [{e}]")
  log.info("서버 종료 완료")
app.router.lifespan_context = lifespan


######################  API: 채널 목록 조회  ######################
@app.get("/api/streams")
async def get_streams():
  return JSONResponse({
    "status": "success",
    "streams": list(stream_manager.keys())
  })


######################  API: 채널의 세그먼트 큐 조회  ######################
@app.get("/api/queue/{stream_name}")
async def get_streams(stream_name:str):
  return JSONResponse({
    "status": "success",
    "streams": stream_manager.get_stream(stream_name).get_queue().get_all_segments()
  })


######################  API: .m3u8 파일 조회  ######################
@app.get("/channel/{stream_name}/index.m3u8")
async def serve_playlist(stream_name: str):
  m3u8_path = os.path.join(STREAMING_CHANNELS, stream_name, "index.m3u8")
  if not os.path.exists(m3u8_path):
    raise HTTPException(status_code=404, detail="Playlist not found")

  response = FileResponse(m3u8_path)
  response.headers["Cache-Control"] = "no-cache"
  return response


######################  API: .m3u8 파일 조회  ######################
@app.get("/channel/1")
async def serve_playlist(stream_name: str):
  m3u8_path = os.path.join(STREAMING_CHANNELS, "channel_1/index.m3u8")
  if not os.path.exists(m3u8_path):
    raise HTTPException(status_code=404, detail="Playlist not found")

  response = FileResponse(m3u8_path)
  response.headers["Cache-Control"] = "no-cache"
  return response


######################  API: 세그먼트 파일 조회  ######################
@app.get("/channel/{channel_name}/{segment}")
async def serve_segment(channel_name: str, segment: str):
  segment_path = os.path.join(STREAMING_CHANNELS, channel_name, HLS_DIR, segment)
  if not os.path.exists(segment_path) or not stream_manager.is_exist(channel_name):
    raise HTTPException(status_code=404, detail="Segment not found")

  response = FileResponse(segment_path)
  response.headers["Cache-Control"] = "no-cache"
  add_metadata_to_response_header(
    headers      = response.headers,        # Response Header
    stream_name  = channel_name,            # 채널 이름
    index        = segment[SEGMENT_FILE_INDEX_START: SEGMENT_FILE_INDEX_END] # 요청한 파일 index
  )
  return response


######################  세그먼트 파일 조회(메타 데이터 조회)  ######################
def add_metadata_to_response_header(headers, stream_name, index):
  stream = stream_manager.get_stream(stream_name)
  index_int = int(index)
  try:
    headers['onair-content-type'] = encode_latin1(stream.get_metadata_by_index_and_column(index_int, MEDIA_TYPE))
    headers['music-title'] = encode_latin1(stream.get_metadata_by_index_and_column(index_int, MEDIA_MUSIC_TITLE))
    headers['music-artist'] = encode_latin1(stream.get_metadata_by_index_and_column(index_int, MEDIA_MUSIC_ARTIST))
    headers['music-cover'] = encode_latin1(stream.get_metadata_by_index_and_column(index_int, MEDIA_MUSIC_COVER))

  except Exception as e:
    log.error(f'메타데이터 조회 에러 [{e}]')
    headers['onair-content-type'] = 'error'
    headers['music-title'] = 'error'
    headers['music-artist'] = 'error'
    headers['music-cover'] = 'error'


def encode_latin1(data):
  return (data.encode('utf-8').decode('latin-1'))