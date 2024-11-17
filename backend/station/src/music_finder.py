import logging
import os

import eyed3

dir_prefix = ".."
base_dir = "/medias/musics"
base_dir_with_prefix = dir_prefix + base_dir


def download_from_keyword(title, artist, cover_url):
    # file_system에 있는지 확인
    file_path = find_exist_file(title, artist)
    # 존재하는 파일이라면
    if file_path is not None:
        audiofile = eyed3.load(dir_prefix + file_path)
        length = audiofile.info.time_secs
        title = audiofile.tag.title
        artist = audiofile.tag.artist
        cover_url = audiofile.tag.album
        return create_return_value(str(file_path), length, title, artist, cover_url)

    # 존재하지 않는 파일이라면
    logging.info(f"{title}, {artist} 곡이 없어요.")
    return None


def find_exist_file(title, artist, content_type='mp3'):
    """title - artist 형태로 파일 경로를 검색하고 반환"""
    # 입력된 title과 artist의 공백 제거
    target_name = f"{title} - {artist}"

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

        # 입력된 title과 artist를 조합한 값과 비교
        if file_name == target_name:
            # 일치하는 파일이 있으면 실제 파일명을 포함한 경로 반환
            return base_dir + f"/{file}"

    return None


def create_return_value(file_path, length, title, artist, cover_url):
    return {"file_path": file_path,
            "length": length,
            "type": "music",
            "music_title": title,
            "music_artist": artist,
            "music_cover_url": cover_url}
