import logging
from datetime import datetime, timedelta


# 여기서 모든 스케쥴을 관리. 개선 필요함.
class DynamicScheduleManager:
    def __init__(self, channel, content_provider, playback_queue, dj):
        self.channel = channel
        self.content_provider = content_provider
        self.playback_queue = playback_queue
        self.dj = dj

        self.start_time = channel.start_time
        if channel.is_default:
            self.end_time = datetime(9999, 12, 31, 23, 59, 59)  # 기본 채널은 종료 시간 없음 (24시간 방송)
        else:
            self.end_time = self.start_time + timedelta(hours=2)

        self.next_news_request = self.start_time
        self.next_weather_request = self.start_time

    def should_request_content(self, current_time):
        """현재 시간이 방송 시간 내에 있는지 확인"""
        return self.start_time <= current_time < self.end_time

    def check_and_enqueue(self):
        """30분 간격으로 뉴스와 날씨 요청"""
        current_time = datetime.now()

        # 방송 시간 내에 있는지 확인
        if not self.should_request_content(current_time):
            logging.info("Broadcasting has ended. No more content requests.")
            return

        # 30분 간격으로 뉴스 요청
        if current_time >= self.next_news_request:
            self.content_provider.request_contents("news")
            logging.info(f"Requested news at {current_time.strftime('%H:%M')}")
            self.next_news_request += timedelta(minutes=30)

        # 30분 간격으로 날씨 요청
        if current_time >= self.next_weather_request:
            self.content_provider.request_contents("weather")
            logging.info(f"Requested weather at {current_time.strftime('%H:%M')}")
            self.next_weather_request += timedelta(minutes=30)
