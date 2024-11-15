# 외부 패키지
from contextlib import asynccontextmanager
from fastapi import FastAPI
from fastapi.responses import JSONResponse, FileResponse
from fastapi.exceptions import HTTPException
from fastapi.middleware.cors import CORSMiddleware
import os

# 내부 패키지
from config import BASIC_CHANNEL_NAME, STREAMING_CHANNELS, HLS_DIR, \
  SEGMENT_FILE_INDEX_END, SEGMENT_FILE_INDEX_START, MEDIA_TYPE, \
  MEDIA_MUSIC_TITLE, MEDIA_MUSIC_ARTIST, MEDIA_MUSIC_COVER
from logger import log
from shared_vars import channels, channel_setup_executor, channel_data_executor

from audio_listener import create_audio_listener_consumer
from channel_manager import remove_channel
from file_utils import clear_hls_path
from segment_queue import SegmentQueue

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
  global channels
  log.info("미디어 서버 초기화 루틴 시작")
  consumer = create_audio_listener_consumer()
  log.info("미디어 서버 가동")

  yield
  log.info(f"서버 종료 루틴 시작 [{channels}]")
  for channel in channels.values():
    remove_channel(channel)
  del channels

  channel_setup_executor.shutdown(True)
  channel_data_executor.shutdown(True)
  log.info(f"ThreadPool 해제")

  consumer.stop_event.set()
  consumer.close()
  log.info("카프카 컨슈머 종료")
  log.info("서버 종료")

app.router.lifespan_context = lifespan


######################  API: 채널 목록 조회  ######################
@app.get("/api/streams")
async def get_streams():
  return JSONResponse({
    "status": "success",
    "streams": list(channels.keys())
  })


######################  API: 채널의 세그먼트 큐 조회  ######################
@app.get("/api/queue")
async def get_streams():
  return JSONResponse({
    "status": "success",
    "streams": channels["channel_1"]["queue"].get_all_segments()
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
  if not os.path.exists(segment_path):
    raise HTTPException(status_code=404, detail="Segment not found")

  response = FileResponse(segment_path)
  response.headers["Cache-Control"] = "no-cache"
  add_metadata_to_response_header(
    headers=      response.headers,        # Response Header
    channel_name= channel_name,            # 채널 이름
    index =       segment[SEGMENT_FILE_INDEX_START: SEGMENT_FILE_INDEX_END] # 요청한 파일 index
  )
  return response


######################  세그먼트 파일 조회(메타 데이터 조회)  ######################
def add_metadata_to_response_header(headers, channel_name, index):
  channel = channels[channel_name]
  queue:SegmentQueue = channel['queue']
  index_int = int(index)
  try:
    headers['onair-content-type'] = encode_latin1(queue.get_metadata_from_index(index_int, MEDIA_TYPE))
    headers['music-title'] = encode_latin1(queue.get_metadata_from_index(index_int, MEDIA_MUSIC_TITLE))
    headers['music-artist'] = encode_latin1(queue.get_metadata_from_index(index_int, MEDIA_MUSIC_ARTIST))
    headers['music-cover'] = encode_latin1(queue.get_metadata_from_index(index_int, MEDIA_MUSIC_COVER))

  except Exception as e:
    log.error(f'메타데이터 조회 에러 [{e}]')
    headers['onair-content-type'] = 'error'
    headers['music-title'] = 'error'
    headers['music-artist'] = 'error'
    headers['music-cover'] = 'error'


def encode_latin1(data):
  return (data.encode('utf-8').decode('latin-1'))