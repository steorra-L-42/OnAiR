import logging
import os
import re
import subprocess
import uuid

import eyed3
import requests
from playwright.sync_api import sync_playwright
from youtubesearchpython import VideosSearch

dir_prefix = ".."
base_dir = "/medias/musics"
base_dir_with_prefix = dir_prefix + base_dir


def download_music_with_retries(video_url):
    """
    음악 다운로드를 최대 2번 시도합니다.
    """
    retries = 0
    max_retries = 2
    while retries < max_retries:
        try:
            temp_music_dir = download_music(video_url)

            # 다운로드 성공 시 경로 반환
            if temp_music_dir is not None:
                return temp_music_dir
            else:
                raise Exception("Failed to download music. The directory is None.")

        except Exception as e:
            retries += 1
            if retries < max_retries:
                logging.info(f"재시도 중... (시도 {retries + 1}/{max_retries})")

    # 최대 재시도 횟수를 초과한 경우 None 반환
    logging.error(f"다운로드 실패: 최대 재시도 횟수({max_retries}) 초과")
    return None


def download_from_keyword(title, artist, cover_url):
    # 먼저 file_system에 있는지 확인
    file_path = find_exist_file_from_keyword(title, artist)
    # 존재하는 파일이라면
    if file_path is not None:
        audiofile = eyed3.load(dir_prefix + file_path)
        length = audiofile.info.time_secs
        title = audiofile.tag.title
        artist = audiofile.tag.artist
        cover_url = audiofile.tag.album
        return create_return_value(str(file_path), length, title, artist, cover_url)
    # 존재하지 않는 파일이라면
    keyword = f"{title} - {artist}"
    # 유튜브에서 키워드로 검색하여 가장 상단의 결과를 가져옴
    video_url = get_video_url_from_keyword(keyword)

    # 출력 파일 이름 설정
    safe_file_name = f"{"".join(c if c.isalnum() or c in ["-", " "] else "" for c in keyword)}.mp3"
    output_filename = f"{safe_file_name}.mp3"

    try:
        # 음악 다운로드
        logging.info(f"Start Download : {safe_file_name}")
        temp_music_dir = download_music_with_retries(video_url)
        logging.info(f"Finished Download : {safe_file_name}")

        # 음악 다운로드 url이 없어서 실패할 경우
        if temp_music_dir is None:
            raise Exception("Failed to download music. The directory is None.")

        # mp3로 convert
        output_music_dir = f"{base_dir_with_prefix}/{safe_file_name}"
        logging.info(f"변환 시작 : {safe_file_name}")
        convert_to_mp3(temp_music_dir, output_music_dir)
        logging.info(f"변환 끝. temp_name : {temp_music_dir}, output_name : {safe_file_name}")

        # 메타데이터 셋팅
        length = set_metadata_and_get_length(output_music_dir, title, artist, cover_url)

        logging.info(f"Downloaded audio as '{output_filename}'")
        return create_return_value(f"{base_dir}/{safe_file_name}", length, title, artist, cover_url)

    except Exception as e:
        logging.error(f"An error occurred: {e}")


def sanitize_string(input_str):
    """모든 공백 및 특수 문자 제거 (한글, 영문, 숫자만 남김)"""
    # 모든 공백 제거 후 소문자로 변환
    sanitized = re.sub(r'\s+', '', input_str).strip().lower()
    # 한글, 영문, 숫자를 제외한 모든 특수문자 제거
    sanitized = re.sub(r'[^가-힣a-zA-Z0-9]', '', sanitized)
    return sanitized


def find_exist_file_from_keyword(title, artist, content_type='mp3'):
    """title - artist 형태로 파일 경로를 검색하고 반환"""
    # 입력된 title과 artist의 공백 제거
    sanitized_title = sanitize_string(title)
    sanitized_artist = sanitize_string(artist)
    target_name = f"{sanitized_title}-{sanitized_artist}"

    # 디렉토리 내의 파일 목록 가져오기
    if not os.path.isdir(base_dir_with_prefix):
        logging.info(f"경로가 존재하지 않습니다: {base_dir_with_prefix}")
        return None

    # 파일명을 공백 제거 후 검색
    for file in os.listdir(base_dir_with_prefix):
        if not file.endswith(f".{content_type}"):
            continue

        # 파일명에서 확장자 제거 후 공백 제거
        file_name, ext = os.path.splitext(file)
        # sanitized_file_name = sanitize_string(file_name)
        sanitized_file_name = file_name.replace(" ", "").strip().lower()

        # 입력된 title과 artist를 조합한 값과 비교
        if sanitized_file_name == target_name:
            # 일치하는 파일이 있으면 실제 파일명을 포함한 경로 반환
            return base_dir + f"/{file}"

    return None


def get_video_url_from_keyword(keyword):
    videos_search = VideosSearch(keyword, limit=1)
    result = videos_search.result()

    # 검색 결과가 없는 경우 처리
    if not result['result']:
        logging.info("No videos found for the keyword.")
        return

    # 최상단 비디오의 URL 가져오기
    video_info = result['result'][0]
    video_url = video_info['link']

    return video_url


def convert_to_mp3(input_file, output_file):
    """FFmpeg를 사용하여 파일을 MP3로 변환"""
    try:
        subprocess.run(
            ["ffmpeg", "-y", "-i", input_file, "-acodec", "libmp3lame", "-b:a", "192k", output_file],
            check=True,
            stdout=subprocess.DEVNULL,
            stderr=subprocess.STDOUT
        )
        logging.info(f"Conversion successful: {output_file}")
    except subprocess.CalledProcessError as e:
        logging.info(f"Error during conversion: {output_file}")
        if os.path.exists(input_file):
            os.remove(input_file)
        if os.path.exists(output_file):
            os.remove(output_file)


def create_return_value(file_path, length, title, artist, cover_url):
    return {"file_path": file_path,
            "length": length,
            "type": "music",
            "music_title": title,
            "music_artist": artist,
            "music_cover_url": cover_url}


def set_metadata_and_get_length(file_path, title, artist, cover_url):
    """MP3 파일에 메타데이터 추가"""
    audiofile = eyed3.load(file_path)
    if audiofile is None:
        logging.info(f"Cannot load audio file: {file_path}")
        return

    # 표준 메타데이터 설정
    audiofile.tag.title = title
    audiofile.tag.artist = artist
    audiofile.tag.album = cover_url

    audiofile.tag.save()
    logging.info(f"Added metadata to audio file: {file_path}")

    return audiofile.info.time_secs


def download_music(url):
    save_directory = base_dir_with_prefix
    os.makedirs(save_directory, exist_ok=True)

    with (sync_playwright() as p):
        try:
            # 브라우저 실행
            browser = p.chromium.launch(headless=True)
            context = browser.new_context()

            # 페이지 열기
            page = context.new_page()

            # 속도 빠르게 하기 위해 애니메이션 등 비활성화
            page.add_init_script("""
                document.querySelectorAll('*').forEach(el => {
                    el.style.transition = 'none';
                    el.style.animation = 'none';
                });
            """)

            page.goto("https://yt5s.biz/enxj100/", wait_until="domcontentloaded")

            # URL 입력 및 제출
            page.fill("#txt-url", url)
            page.click("#btn-submit")

            # MP3 버튼 대기 및 클릭
            mp3_button = page.locator("button[data-ftype='mp3'][data-fquality='128']")
            mp3_button.wait_for(state="visible", timeout=10_000)  # 대기 시간 축소
            mp3_button.click()

            # 모달 대기 및 다운로드 URL 추출
            page.wait_for_selector("a#A_downloadUrl", timeout=10_000)  # 대기 시간 축소
            download_anchor = page.locator("a#A_downloadUrl")
            download_url = download_anchor.evaluate("anchor => anchor.href")

            if not download_url:
                logging.info("Cannot find download URL. Skipping...")
                logging.debug(page.content())
                return None

            # MP3 파일 다운로드
            file_name = f"{str(uuid.uuid4())}.mp3"
            save_path = os.path.join(save_directory, file_name)
            response = requests.get(download_url, stream=True)
            response.raise_for_status()

            with open(save_path, "wb") as f:
                for chunk in response.iter_content(chunk_size=262144):  # 256KB 단위
                    f.write(chunk)

            return f"{base_dir_with_prefix}/{file_name}"

        except Exception as e:
            logging.info(f"Error occurred while downloading music")
        finally:
            page.close()
            context.close()
            browser.close()
