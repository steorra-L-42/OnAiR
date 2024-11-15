FROM python:3.12
ENV PYTHONENCODING=utf-8

# 작업 디렉토리 설정
WORKDIR /station

# requirements.txt 복사 및 설치
COPY requirements.txt /station

RUN pip install --upgrade pip
RUN pip install -r /station/requirements.txt

# 소스 코드 복사
COPY src /station/src

WORKDIR src
# 볼륨 설정용 기본 파일 복사
COPY ./station/medias/start.mp3 /station/medias/start.mp3
# 볼륨 설정
VOLUME ["/station/medias"]

CMD ["python3", "main.py"]