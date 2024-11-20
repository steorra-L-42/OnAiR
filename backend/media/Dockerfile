# Python 3.12 이미지를 기반으로 생성
FROM python:3.12

# UTF-8 환경 설정
ENV PYTHONIOENCODING=utf-8
ENV TZ=Asia/Seoul

# 작업 디렉토리 설정
WORKDIR /media

# 필요한 패키지 업데이트 및 ffmpeg 설치
RUN apt-get update && \
    apt-get install -y ffmpeg && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# requirements.txt 복사 및 설치
COPY requirements.txt .

RUN pip install --upgrade pip
RUN pip install -r requirements.txt

# 소스 코드 복사
COPY src .

# FastAPI 포트 노출
EXPOSE 8000

# 컨테이너 내부 볼륨 설정
VOLUME ["/media/streaming_channels"]

# uvicorn 실행
CMD ["uvicorn", "server:app", "--host", "0.0.0.0", "--port", "8000"]
