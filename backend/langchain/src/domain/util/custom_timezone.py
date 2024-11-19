import pytz
from datetime import datetime, timedelta

import os
import sys
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(os.path.dirname(__file__)))))
from config import TIMEZONE

# 한국 시간
MORNING = "06:00"
EVENING = "18:00"
MIDNIGHT = "00:00"

import pytz
from datetime import datetime, timedelta
seoul_tz = pytz.timezone('Asia/Seoul')
SEOUL_NOW = (datetime.now(seoul_tz)).strftime('%Y-%m-%d %H:%M:%S')

import pytz
from datetime import datetime, timedelta
seoul_tz = pytz.timezone('Asia/Seoul')
SEOUL_YESTERDAY = (datetime.now(seoul_tz) - timedelta(days=1)).strftime('%Y-%m-%d %H:%M:%S')

import pytz
from datetime import datetime, timedelta
seoul_tz = pytz.timezone('Asia/Seoul')
TWELVE_HOURS_AGO = (datetime.now(seoul_tz) - timedelta(hours=12)).strftime('%Y-%m-%d %H:%M:%S')

def get_times():
    # 실행환경의 시간대에 따라서 시간을 반환
    if TIMEZONE == 'UTC':
        return get_utc_times()
    if TIMEZONE == 'Asia/Seoul':
        return {
            "MORNING": MORNING,
            "EVENING": EVENING,
            "MIDNIGHT": MIDNIGHT
        }

def get_utc_times():
    # Seoul time to UTC
    seoul_tz = pytz.timezone('Asia/Seoul')
    morning_datetime = datetime.strptime(MORNING, "%H:%M")
    evening_datetime = datetime.strptime(EVENING, "%H:%M")
    midnight_datetime = datetime.strptime(MIDNIGHT, "%H:%M")

    # expext
    # 21:00
    # 09:00
    # 15:00
    UTC_MORNING = (morning_datetime - timedelta(hours=9)).strftime('%H:%M')
    UTC_EVENING = (evening_datetime - timedelta(hours=9)).strftime('%H:%M')
    UTC_MIDNIGHT = (midnight_datetime - timedelta(hours=9)).strftime('%H:%M')

    return {
        "MORNING": UTC_MORNING,
        "EVENING": UTC_EVENING,
        "MIDNIGHT": UTC_MIDNIGHT
    }