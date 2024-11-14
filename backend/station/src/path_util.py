import os
from pathlib import Path


def get_medias_path(current_dir):
    """부모 디렉토리에서 'medias' 폴더를 생성하고 경로를 반환합니다."""
    src_path = Path(current_dir).parent
    medias_path = src_path / "medias"

    # 'medias' 폴더 생성 (이미 존재하면 에러 발생하지 않음)
    os.makedirs(medias_path, exist_ok=True)

    return medias_path
