FROM ubuntu:22.04

# Set Environment Variables
ENV DEBIAN_FRONTEND=noninteractive
ENV TZ=Asia/Seoul

# Install dependencies
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    python3.11 \
    python3-pip \
    chromium-browser \
    chromium-chromedriver && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# update pip to 24.2
RUN python3 -m pip install --upgrade pip==24.2

# Set default Python and pip commands
RUN update-alternatives --install /usr/bin/python3 python3 /usr/bin/python3.11 1 && \
    update-alternatives --install /usr/bin/pip pip /usr/bin/pip3 1

# UTF-8 환경 설정
ENV PYTHONENCODING=utf-8

# Set a working directory
WORKDIR /app

# Make src directory and copy the source code
RUN mkdir src
COPY src /app/src

# Copy the current directory contents into the container at /app
COPY requirements.txt /app/

# Install any needed packages specified in requirements.txt
RUN pip install --no-cache-dir -r requirements.txt

CMD ["python3", "/app/src/main.py"]