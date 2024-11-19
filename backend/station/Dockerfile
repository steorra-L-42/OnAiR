FROM python:3.12
ENV PYTHONIOENCODING=utf-8
ENV TZ=Asia/Seoul

# 작업 디렉토리 설정
WORKDIR /station

# 필요한 패키지 업데이트 및 설치 (ffmpeg 포함)
RUN apt-get update && \
    apt-get install -y ffmpeg \
    libnss3 libatk1.0-0 libcups2 libxcomposite1 \
    libxrandr2 libxdamage1 libgbm1 libpango-1.0-0 libxcursor1 libwoff1 \
    libwebpdemux2 libharfbuzz-icu0 libenchant-2-2 libsecret-1-0 libhyphen0 \
    libegl1 libevdev2 libgles2 \
    libgtk-3-0 libgdk-pixbuf-2.0-0 libatk-bridge2.0-0 libatspi2.0-0 && \
    apt-get install -y \
    gstreamer1.0-plugins-base \
    gstreamer1.0-plugins-good \
    gstreamer1.0-plugins-bad \
    gstreamer1.0-plugins-ugly \
    gstreamer1.0-libav \
    gstreamer1.0-tools \
    gstreamer1.0-gl \
    gstreamer1.0-gtk3 \
    gstreamer1.0-pulseaudio \
    gstreamer1.0-alsa \
    libmanette-0.2-0 && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*


# requirements.txt 복사 및 설치
COPY requirements.txt /station/

RUN pip install --upgrade pip
RUN pip install -r /station/requirements.txt

COPY . . 

RUN playwright install

WORKDIR /station/src
CMD ["python3", "main.py"]