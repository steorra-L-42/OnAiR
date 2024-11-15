FROM python:3.12
ENV PYTHONENCODING=utf-8

# 작업 디렉토리 설정
WORKDIR /station

# requirements.txt 복사 및 설치
COPY . . 

RUN pip install --upgrade pip
RUN pip install -r /station/requirements.txt

WORKDIR /station/src
CMD ["python3", "main.py"]