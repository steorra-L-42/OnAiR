FROM python:3.12.5
ENV PYTHONENCODING=utf-8

# 작업 디렉토리 설정
WORKDIR /media

# requirements.txt 복사 및 설치
COPY src/requirements.txt /media/src/

RUN pip install --upgrade pip
RUN pip install -r /media/src/requirements.txt

# 소스 코드 복사
COPY src /media/src

WORKDIR src

# FastAPI 포트 노출
EXPOSE 8000

# uvicorn 실행
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]