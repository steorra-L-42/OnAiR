import datetime

import firebase_admin
from firebase_admin import messaging, credentials
from logger import log
from config import SERVICE_ACCOUNT_KEY_PATH

# Firebase 앱 초기화
cred = credentials.Certificate(SERVICE_ACCOUNT_KEY_PATH)
firebase_app = firebase_admin.initialize_app(cred)

def notify_stream_start(token, fcm_data:dict):
    fcm_data["type"] = "channel_created"
    fcm_data["timestamp"] = str(datetime.datetime.now())
    send_fcm_notification_only_data(token, fcm_data)


def send_fcm_notification(topic, title, body, data=None):
    try:
        message = messaging.Message(
            notification=messaging.Notification(
                title=title,
                body=body,
            ),
            data = data,
            topic=topic
        )
        response = messaging.send(message)
        log.info(f"FCM 알림 전송 완료: {response}")
    except Exception as e:
        log.error(f"FCM 알림 전송 실패: {e}")


def send_fcm_notification_only_data(token, data):
    try:
        message = messaging.Message(
            token = token,
            data  = data
        )
        response = messaging.send(message)
        log.info(f"FCM 알림 전송 완료: {response}")
    except Exception as e:
        log.error(f"FCM 알림 전송 실패: {e}")