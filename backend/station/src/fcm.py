import json
import logging

import firebase_admin
from firebase_admin import credentials, messaging

from config import firebase_credentials


def initialize_firebase():
    """
    Firebase Admin SDK 초기화 (dict를 사용하여 인증)
    """
    if not firebase_admin._apps:
        service_account_info = json.loads(firebase_credentials)
        cred = credentials.Certificate(service_account_info)
        firebase_admin.initialize_app(cred)


def send_fcm_notification(fcm_token, title, body, data=None):
    """
    단일 기기에 FCM 알림을 보냅니다.
    """
    initialize_firebase()

    # 메시지 구성
    message = messaging.Message(
        token=fcm_token,
        notification=messaging.Notification(
            title=title,
            body=body
        ),
        data=data if data else {}
    )

    try:
        # FCM 알림 전송
        response = messaging.send(message)
        logging.info(f"Success to push FCM: {response}", response)
    except Exception as e:
        logging.info(f"Failed to push FCM: {e}")


def push_fcm(channel_uuid, channel_name, value):
    story_title = value.get("story_title")
    fcm_token = value.get("fcm_token")

    send_fcm_notification(fcm_token,
                          None,
                          None,
                          {"type": "story_chosen",
                           "channel_uuid": channel_uuid,
                           "channel_name": channel_name,
                           "story_title": story_title})
