import json
import logging
from datetime import datetime, timedelta
from threading import Thread, Event, current_thread

import schedule
from mutagen.mp3 import MP3

from instance import channel_manager, producer


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

        self.stop_event = Event()
        self.scheduler_thread = None

        self.schedule_content_requests()
        self.schedule_channel_closure()
        self.start_scheduler()

        # 송출 시간 누적 변수
        self.cumulative_time_diff = 0  # 초기 차이는 0초

    def schedule_content_requests(self):
        """30분 간격으로 뉴스와 날씨 요청 예약"""
        self.request_contents()
        schedule.every(30).minutes.do(self.request_contents)

    def schedule_channel_closure(self):
        """채널 종료 스케줄"""
        if self.channel.is_default:
            schedule.every(24).hours.do(channel_manager.remove_channel, self.channel.channel_id)
        else:
            schedule.every(2).hours.do(channel_manager.remove_channel, self.channel.channel_id)

    def request_contents(self):
        current_time = datetime.now()
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

    # 콘텐츠 송출 및 순환 재생
    def process_broadcast(self):
        story_queue = self.playback_queue.queues["story"]
        news_queue = self.playback_queue.queues["news"]
        weather_queue = self.playback_queue.queues["weather"]
        playlist = self.playback_queue.playlist
        playlist_index = 0  # playlist 순환 인덱스

        # 누적된 시간 차이 초기화
        self.cumulative_time_diff = 0

        # while True:
        #     # story -> news -> weather -> playlist 순서로 콘텐츠 송출
        #     for queue in [story_queue, news_queue, weather_queue, playlist]:
        #         file_path = None
        #         print("씨발1")
        #
        #         if queue and queue != playlist:
        #             print("씨발2")
        #             file_path = queue.pop(0)  # 큐에서 하나 꺼내기
        #             print("씨발3")
        #             logging.info(f"Processing from queue: {file_path}")
        #
        #         elif queue == playlist and playlist:
        #             print("씨발4")
        #             file_path = playlist[playlist_index]
        #             print("씨발5")
        #             playlist_index = (playlist_index + 1) % len(playlist)  # 순환 인덱스
        #             logging.info(f"Processing from playlist: {file_path}")
        #
        #         print("씨발6")
        #         if file_path:
        #             # MP3 파일 길이 측정 후 송출
        #             print("씨발7")
        #             print(file_path)
        #             duration = self.get_mp3_duration(str(file_path))
        #             logging.info(f"File {file_path} duration: {duration:.2f} seconds")
        #
        #             # 송출할 파일의 길이에 맞춰 대기
        #             # 누적된 시간 차이가 60초 이상이면 대기 시간을 조정하여 보정
        #             if self.cumulative_time_diff >= 60:
        #                 # 누적 시간 차이가 60초 이상이면, 차이를 맞추기 위해 대기 -> 10초로 차이를 둔다.
        #                 time.sleep(self.cumulative_time_diff - 10)
        #                 self.cumulative_time_diff = 10
        #
        #             # 콘텐츠 송출 (Kafka에 보내기)
        #             self.produce_contents(file_path)
        #
        #             # 콘텐츠 길이만큼 비동기 대기
        #             time.sleep(duration - 10)  # 10초를 빼고 대기, 이를 조정하여 정확한 송출 타이밍 맞추기
        #             # 누적 시간 차이를 계산
        #             self.cumulative_time_diff += 10

    def produce_contents(self, file_path):
        """콘텐츠를 Kafka에 송출"""
        value = json.dumps({
            "filePath": str(file_path),
            "isStart": False
        })
        producer.send_message("media_topic",
                              self.channel.channel_id.encode("utf-8"),
                              value.encode("utf-8"))

    def get_mp3_duration(self, file_path):
        """MP3 파일의 길이 가져오기"""
        audio = MP3(file_path)
        return audio.info.length

    def stop(self):
        """스케줄러 종료 및 리소스 정리"""
        logging.info(f"Stopping DynamicScheduleManager for channel {self.channel.channel_id}")

        schedule.clear()
        logging.info("Cleared all scheduled tasks.")
        self.stop_event.set()

        if self.scheduler_thread and self.scheduler_thread is not current_thread():
            self.scheduler_thread.join()
            logging.info("Scheduler thread has been stopped.")

        logging.info(f"DynamicScheduleManager for channel {self.channel.channel_id} has been stopped.")
