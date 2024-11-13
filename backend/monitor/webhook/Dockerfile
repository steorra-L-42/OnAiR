FROM python:3.12.5

ENV PYTHONENCODING=utf-8

WORKDIR /monitor/webhook

COPY . .

RUN pip install --upgrade pip
RUN pip install -r requirements.txt

EXPOSE 5000
CMD ["python", "webhook.py"]