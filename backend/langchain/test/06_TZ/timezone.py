import pytz
from datetime import datetime, timedelta

# 한국 시간
MORNING = "06:00"
EVENING = "18:00"
MIDNIGHT = "00:00"

# Seoul time to UTC
seoul_tz = pytz.timezone('Asia/Seoul')
morning_datetime = datetime.strptime(MORNING, "%H:%M")
evening_datetime = datetime.strptime(EVENING, "%H:%M")
midnight_datetime = datetime.strptime(MIDNIGHT, "%H:%M")

# expext
# 21:00
# 09:00
# 15:00
utc_morning_time = (morning_datetime - timedelta(hours=9)).strftime('%H:%M')
utc_evening_time = (evening_datetime - timedelta(hours=9)).strftime('%H:%M')
utc_midnight_time = (midnight_datetime - timedelta(hours=9)).strftime('%H:%M')

def get_seoul_time():
    return datetime.now(pytz.timezone('Asia/Seoul'))