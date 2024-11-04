import subprocess
import os
import threading
import time
from flask import Flask, jsonify, request, send_from_directory
import socket
import signal
import logging
from datetime import datetime
import shutil

# 로깅 설정
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# HLS 설정
HLS_BASE_DIR = './channels'
HLS_TIME = 2
HLS_LIST_SIZE = 5
HLS_DELETE_THRESHOLD = 360

streams = {}
stop_event = threading.Event()
app = Flask(__name__)

# 종료 신호 처리
def signal_handler(signum, frame):
    logger.info("종료 신호 수신. 정리 작업 수행 중...")
    stop_event.set()
    cleanup_streams()

signal.signal(signal.SIGINT, signal_handler)
signal.signal(signal.SIGTERM, signal_handler)

# 사용 가능한 포트 찾기
def find_available_port(default_port=5000):
    port = default_port
    while True:
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            if s.connect_ex(('localhost', port)) != 0:
                return port
            port += 1

# 스트림 정리 작업
def cleanup_streams():
    for stream_name in list(streams.keys()):
        remove_stream(stream_name)

    # channels 폴더 삭제/재생성
    if os.path.exists(HLS_BASE_DIR):
        shutil.rmtree(HLS_BASE_DIR)
        os.makedirs(HLS_BASE_DIR)

# concat 파일 생성
def create_concat_file(stream_name, playlist_dir):
    hls_output_dir = os.path.join(HLS_BASE_DIR, stream_name)
    concat_file_path = os.path.join(hls_output_dir, 'concat.txt')

    try:
        playlist = sorted([f for f in os.listdir(playlist_dir) if f.endswith(('.mp3', '.m4a', '.aac'))])
        if not playlist:
            logger.error(f"{playlist_dir}에 오디오 파일이 없는데?")
            return None

        with open(concat_file_path, 'w') as f:
            for track in playlist:
                track_path = os.path.abspath(os.path.join(playlist_dir, track))
                f.write(f"file '{track_path}'\n")

        return concat_file_path
    except Exception as e:
        logger.error(f"concat 파일 생성 오류: {e}")
        return None

# HLS 스트림 생성
def generate_hls_stream(stream_name, playlist_dir):
    hls_output_dir = os.path.join(HLS_BASE_DIR, stream_name)
    os.makedirs(hls_output_dir, exist_ok=True)

    while not stop_event.is_set() and stream_name in streams:
        try:
            # 기존 파일 삭제
            for file in os.listdir(hls_output_dir):
                if file.endswith(('.ts', '.m3u8', '.aac')):
                    os.remove(os.path.join(hls_output_dir, file))

            # concat 파일 생성
            concat_file_path = create_concat_file(stream_name, playlist_dir)
            if not concat_file_path:
                raise Exception(f"{stream_name}의 concat 파일 생성 실패")

            # FFmpeg 명령어로 연속 스트리밍
            ffmpeg_command = [
                'ffmpeg',
                '-re',
                '-f', 'concat',
                '-safe', '0',
                '-i', concat_file_path,
                '-c:a', 'aac',
                '-b:a', '128k',
                '-f', 'hls',
                '-hls_time', str(HLS_TIME),
                '-hls_list_size', str(HLS_LIST_SIZE),
                '-hls_flags', 'delete_segments+append_list+program_date_time',
                '-hls_segment_type', 'mpegts',
                '-hls_segment_filename', os.path.join(hls_output_dir, 'segment_%05d.ts'),
                '-method', 'PUT',
                os.path.join(hls_output_dir, 'index.m3u8')
            ]

            logger.info(f"{stream_name} 방송 FFmpeg 프로세스 시작")
            process = subprocess.Popen(
                ffmpeg_command,
                stdout=subprocess.PIPE,
                stderr=subprocess.PIPE,
                universal_newlines=True
            )

            streams[stream_name]['process'] = process

            # 프로세스 모니터링 및 세그먼트 관리
            while not stop_event.is_set() and stream_name in streams:
                if process.poll() is not None:
                    error = process.stderr.read()
                    logger.error(f"{stream_name} 방송 FFmpeg 프로세스 갑자기 종료됨!!!!!!!!!!!!!!!!!!: {error}")
                    break

                # 버퍼 오버플로우 방지를 위해 출력 읽기
                output = process.stderr.readline()
                if output:
                    logger.debug(f"{stream_name} FFmpeg 출력: {output.strip()}")

                manage_segments(hls_output_dir)
                time.sleep(0.1)  # 모니터링 반응성 향상을 위한 대기 시간 감소

            if process.poll() is None:
                process.terminate()
                try:
                    process.wait(timeout=5)
                except subprocess.TimeoutExpired:
                    process.kill()

        except Exception as e:
            logger.error(f"{stream_name} 스트림 오류: {e}")
        finally:
            if stream_name in streams and 'process' in streams[stream_name]:
                try:
                    streams[stream_name]['process'].terminate()
                    streams[stream_name]['process'].wait(timeout=5)
                except:
                    pass

        if not stop_event.is_set() and stream_name in streams:
            logger.info(f"{stream_name} 스트림 오류 발생. 재시작 하는중")
            time.sleep(2)  # 재시작 전 대기 시간

# 세그먼트 관리
def manage_segments(hls_output_dir):
    try:
        segments = sorted([f for f in os.listdir(hls_output_dir) if f.endswith('.ts')])
        if len(segments) > HLS_DELETE_THRESHOLD:
            to_delete = segments[:-HLS_DELETE_THRESHOLD]
            for segment in to_delete:
                try:
                    os.remove(os.path.join(hls_output_dir, segment))
                except OSError:
                    pass
        update_playlist(hls_output_dir)
    except Exception as e:
        logger.error(f"세그먼트 관리 오류: {e}")

# 재생 목록 업데이트
def update_playlist(hls_output_dir):
    playlist_path = os.path.join(hls_output_dir, 'index.m3u8')
    if not os.path.exists(playlist_path):
        return

    try:
        with open(playlist_path, 'r') as f:
            content = f.read()

        if '#EXTM3U' not in content:
            logger.warning(f"{playlist_path}에 이상한 재생 목록 형식 있는데?")
            return

        segments = [line.strip() for line in content.split('\n')
                    if line.strip().endswith('.ts')]

        if not segments:
            logger.warning(f"{playlist_path}에 세그먼트 없는데?")
            return

        existing_segments = set(os.listdir(hls_output_dir))
        valid_segments = [seg for seg in segments
                          if os.path.basename(seg) in existing_segments]

        if not valid_segments:
            logger.warning(f"{hls_output_dir}에 유효한 세그먼트 없음")
            return

    except Exception as e:
        logger.error(f"재생 목록 업데이트 오류: {e}")

# API 엔드포인트: 모든 스트림 조회
@app.route('/api/streams', methods=['GET'])
def get_streams():
    return jsonify({
        'status': 'success',
        'streams': list(streams.keys())
    })

# API 엔드포인트: 방송 시작
@app.route('/api/streams/<stream_name>', methods=['POST'])
def start_stream(stream_name):
    playlist_dir = request.json.get('playlist_dir')
    if not playlist_dir:
        return jsonify({
            'status': 'error',
            'message': 'playlist_dir 필수'
        }), 400

    result = add_stream(stream_name, playlist_dir)
    return jsonify(result)

# API 엔드포인트: 방송 종료
@app.route('/api/streams/<stream_name>', methods=['DELETE'])
def stop_stream(stream_name):
    result = remove_stream(stream_name)
    return jsonify(result)

# 채널 재생 목록 서비스
@app.route('/channel/<stream_name>/index.m3u8')
def serve_playlist(stream_name):
    response = send_from_directory(os.path.join(HLS_BASE_DIR, stream_name), 'index.m3u8')
    response.headers['Cache-Control'] = 'no-cache'
    return response

# 채널 세그먼트 서비스
@app.route('/channel/<stream_name>/<segment>')
def serve_segment(stream_name, segment):
    return send_from_directory(os.path.join(HLS_BASE_DIR, stream_name), segment)

# 방송 추가
def add_stream(stream_name, playlist_dir, base_url=None):
    if stream_name in streams:
        return {
            'status': 'error',
            'message': f'{stream_name} 이름으로 된 방송 이미 존재'
        }

    if not os.path.exists(playlist_dir):
        return {
            'status': 'error',
            'message': f'{playlist_dir} 재생 목록 디렉터리 존재하지 않음'
        }

    streams[stream_name] = {}
    thread = threading.Thread(
        target=generate_hls_stream,
        args=(stream_name, playlist_dir),
        daemon=True
    )
    streams[stream_name]['thread'] = thread
    thread.start()

    url = f"{base_url or request.host_url}channel/{stream_name}/index.m3u8"
    logger.info(f"{stream_name} 방송 추가됨. URL: {url}")
    return {
        'status': 'success',
        'message': f'{stream_name} 방송 추가됨',
        'url': url
    }

# 방송 삭제
def remove_stream(stream_name):
    if stream_name not in streams:
        return {
            'status': 'error',
            'message': f'{stream_name} 방송 없는데?'
        }

    if 'process' in streams[stream_name]:
        try:
            streams[stream_name]['process'].terminate()
            streams[stream_name]['process'].wait(timeout=5)
        except:
            pass

    del streams[stream_name]

    # 방송 폴더 삭제
    stream_dir = os.path.join(HLS_BASE_DIR, stream_name)
    if os.path.exists(stream_dir):
        shutil.rmtree(stream_dir)

    logger.info(f"{stream_name} 방송 삭제 완료")
    return {
        'status': 'success',
        'message': f'{stream_name} 방송 삭제 완료'
    }

# 테스트 설정
def test_setup(base_url):
    sample_channels = [
        ("channel_1", "./radio_playlist1"),
        ("channel_2", "./radio_playlist2")
    ]
    for channel, playlist_dir in sample_channels:
        add_stream(channel, playlist_dir, base_url)

# 메인 실행
if __name__ == '__main__':
    os.makedirs(HLS_BASE_DIR, exist_ok=True)
    port = find_available_port(5000)
    base_url = f"http://k11d204.p.ssafy.io:{port}/"

    logger.info(f"{port} 번 포트로 HLS 서버 시작함")
    test_setup(base_url)

    try:
        app.run(host='0.0.0.0', port=port)
    finally:
        cleanup_streams()

