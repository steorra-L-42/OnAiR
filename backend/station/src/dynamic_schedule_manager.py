import asyncio
import logging
from datetime import datetime, timedelta
from threading import Thread, Event, current_thread

import schedule

from instance import channel_manager


class DynamicScheduleManager:
    def __init__(self, channel, content_provider, playback_queue, dj):
        self.buffering_time = 0
        self.playlist_index = 0
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

        # 최초에 한번 playlist 가장 앞 노래를 보냅니다.
        self.process_first_music(playlist)
        # 채널 시작 후 로직을 실행하기 까지 대기.
        self.async_sleep(30)

        while not self.channel.stop_event.is_set():
            # story -> news -> weather -> playlist 순서로 콘텐츠 송출
            for struct_name, struct in [("story", story_queue), ("news", news_queue),
                                        ("weather", weather_queue), ("playlist", playlist)]:
                file_infos = None

                if struct_name == "story" and struct:
                    file_infos = struct.pop()
                    logging.info(f"Processing from {struct_name} queue: {file_infos}")

                elif struct_name in ["news", "weather"] and struct:
                    file_infos = struct.pop()  # 큐에서 하나 꺼내기
                    logging.info(f"Processing from {struct_name} queue: {file_infos}")

                elif struct == playlist and playlist:
                    file_infos = playlist[self.playlist_index]
                    self.playlist_index = (self.playlist_index + 1) % len(playlist)  # 순환 인덱스
                    logging.info(f"Processing from {struct_name} playlist: {file_infos}")

                if file_infos:
                    file_info_list = []
                    file_lengths = 0
                    for file_info in file_infos:
                        # 기본 정보
                        file_info_entry = {
                            "filePath": file_info.get("file_path"),
                            "type": file_info.get("type")
                        }

                        # type이 'music'일 경우 추가 정보 설정
                        if file_info["type"] == "music":
                            file_info_entry.update({
                                "musicTitle": file_info.get("music_title"),
                                "musicArtist": file_info.get("music_artist"),
                                "musicCoverUrl": file_info.get("music_cover_url")
                            })

                        file_info_list.append(file_info_entry)
                        file_lengths += file_info.get("length")
                    self.dj.produce_contents(file_info_list)
                    logging.info(f"Sleeping for File length: {file_lengths}")
                    self.async_sleep(file_lengths)
                    logging.info(f"Done sleeping for File length: {file_lengths}")

    def async_sleep(self, duration):
        """동기 함수 내에서 비동기 대기 처리"""
        asyncio.run(self._async_sleep(duration))

    async def _async_sleep(self, duration):
        """비동기 대기 함수"""
        await asyncio.sleep(duration)

    def process_first_music(self, playlist):
        first_music = playlist[self.playlist_index][0]
        self.playlist_index += 1
        self.buffering_time = first_music.get("length")
        self.dj.produce_contents({
            "filePath": first_music.get("file_path"),
            "type": "music",
            "musicTitle": first_music.get("music_title"),
            "musicArtist": first_music.get("music_artist"),
            "musicCoverUrl": first_music.get("music_cover_url")})

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
