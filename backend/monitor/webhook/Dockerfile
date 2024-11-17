FROM python:3.12

ENV PYTHONIOENCODING=utf-8
ENV TZ=Asia/Seoul

WORKDIR /webhook

COPY . .

RUN pip install --upgrade pip
RUN pip install -r requirements.txt

EXPOSE 5000
CMD ["uvicorn", "webhook:app", "--host", "0.0.0.0", "--port", "5000"]