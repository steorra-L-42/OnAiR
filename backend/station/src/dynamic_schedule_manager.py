import logging
from datetime import datetime, timedelta
from threading import Thread, Event, current_thread

import schedule

from instance import channel_manager


# 여기서 모든 스케쥴을 관리. 개선 필요함.
class DynamicScheduleManager:
    def __init__(self, channel, content_provider, playback_queue, dj):
        self.channel = channel
        self.content_provider = content_provider
        self.playback_queue = playback_queue
        self.dj = dj

        self.start_time = channel.start_time
        if channel.is_default:
            self.end_time = self.start_time + timedelta(hours=24)
        else:
            self.end_time = self.start_time + timedelta(hours=2)

        # 스레드 종료 이벤트
        self.stop_event = Event()
        self.scheduler_thread = None

        # 스케줄 초기화 및 시작
        self.schedule_content_requests()
        self.schedule_channel_closure()
        self.start_scheduler()

    def schedule_content_requests(self):
        """30분 간격으로 뉴스와 날씨 요청 예약"""
        self.request_contents()
        schedule.every(30).minutes.do(self.request_contents)

    def schedule_channel_closure(self):
        """채널 종료 스케줄"""
        if self.channel.is_default:
            schedule.every(24).hours.do(channel_manager.remove_channel, self.channel.channel_id)
            return

        schedule.every(2).hours.do(channel_manager.remove_channel, self.channel.channel_id)

    def request_contents(self):
        current_time = datetime.now()

        # 방송 시간 내에 있는지 확인
        if not self.should_request_content(current_time):
            logging.info("Broadcasting has ended. No more content requests.")
            return

        # 30분 간격으로 뉴스 요청
        self.content_provider.request_contents("news")
        logging.info(f"{self.channel.channel_id} Requested news at {current_time.strftime('%H:%M')}")

        # 30분 간격으로 날씨 요청
        self.content_provider.request_contents("weather")
        logging.info(f"{self.channel.channel_id} Requested weather at {current_time.strftime('%H:%M')}")

    def should_request_content(self, current_time):
        """현재 시간이 방송 시간 내에 있는지 확인"""
        return self.start_time <= current_time < self.end_time

    def start_scheduler(self):
        """스케줄러를 주기적으로 실행하는 스레드 시작"""

        def run_schedule():
            while not self.stop_event.is_set():
                schedule.run_pending()
                next_run = schedule.idle_seconds()
                if next_run is None:
                    self.stop_event.wait(1)
                else:
                    self.stop_event.wait(max(0, next_run))

        # 스레드 시작
        self.scheduler_thread = Thread(target=run_schedule, daemon=True)
        self.scheduler_thread.start()

    def stop(self):
        """스케줄러 종료 및 리소스 정리"""
        logging.info(f"Stopping DynamicScheduleManager for channel {self.channel.channel_id}")

        # 모든 스케줄 작업 정리
        schedule.clear()
        logging.info("Cleared all scheduled tasks.")

        # 스레드 종료 신호 설정
        self.stop_event.set()
        # 현재 스레드가 scheduler_thread와 다를 때만 join() 호출

        if self.scheduler_thread and self.scheduler_thread is not current_thread():
            self.scheduler_thread.join()
            logging.info("Scheduler thread has been stopped.")

        logging.info(f"DynamicScheduleManager for channel {self.channel.channel_id} has been stopped.")
