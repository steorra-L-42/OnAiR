FROM python:3.12.5
ENV PYTHONENCODING=utf-8

# 작업 디렉토리 설정
WORKDIR /media

# requirements.txt 복사 및 설치
COPY requirements.txt .

RUN pip install --upgrade pip
RUN pip install -r requirements.txt

# 소스 코드 복사
COPY src .

# empty.mp3 파일 복사
COPY ./streaming_channels/channel_1/empty.mp3 /media/streaming_channels/channel_1/empty.mp3

# FastAPI 포트 노출
EXPOSE 8000

# 컨테이너 내부 볼륨 설정
VOLUME ["/media/streaming_channels"]

# uvicorn 실행
CMD ["uvicorn", "server:app", "--host", "0.0.0.0", "--port", "8000"]
