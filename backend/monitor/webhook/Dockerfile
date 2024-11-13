FROM python:3.12.5

ENV PYTHONENCODING=utf-8

WORKDIR /webhook

COPY . .

RUN pip install --upgrade pip
RUN pip install -r requirements.txt

EXPOSE 5000
CMD ["uvicorn", "webhook:app", "--host", "0.0.0.0", "--port", "5000"]