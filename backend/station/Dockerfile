FROM python:3.12
ENV PYTHONENCODING=utf-8

# 작업 디렉토리 설정
WORKDIR /station

# 필요한 패키지 업데이트 및 ffmpeg 설치
RUN apt-get update && \
    apt-get install -y ffmpeg && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# requirements.txt 복사 및 설치
COPY . . 

RUN pip install --upgrade pip
RUN pip install -r /station/requirements.txt

WORKDIR /station/src
CMD ["python3", "main.py"]