import schedule
import time
import sqlite3
import os
import logging
import pytz
import custom_timezone
from datetime import datetime, timedelta

class Scheduler:

    def __init__(self, weather_crawler, news_crawler):
        self.weather_crawler = weather_crawler
        self.news_crawler = news_crawler
        self.MORNING = custom_timezone.get_times()["MORNING"]
        self.EVENING = custom_timezone.get_times()["EVENING"]
        self.MIDNIGHT = custom_timezone.get_times()["MIDNIGHT"]

        # 부팅 시 크롤링
        if self.need_to_crawl_when_start():
            logging.info("News, Weather need to be crawled at start.")
            time.sleep(1)
            self.weather_crawler.crawl()
            self.news_crawler.crawl()
        else:
            logging.info("News, Weather do not need to be crawled at start.")

    def start(self):

        # weather
        schedule.every().day.at(self.MORNING).do(self.weather_crawler.crawl)
        schedule.every().day.at(self.EVENING).do(self.weather_crawler.crawl)

        # news
        schedule.every().day.at(self.MORNING).do(self.news_crawler.crawl)
        schedule.every().day.at(self.EVENING).do(self.news_crawler.crawl)
    
        # 하루 지난 기사, 날씨 정보 삭제
        schedule.every().day.at(self.MIDNIGHT).do(self.news_crawler.delete_yesterday_news)
        schedule.every().day.at(self.MIDNIGHT).do(self.weather_crawler.delete_yesterday_weather)

    # 12시간 이내에 크롤링한 뉴스가 30개 이상, 날씨가 1개 이상 있으면 서버를 시작할 때 크롤링을 하지 않는다.
    def need_to_crawl_when_start(self):

        try:
            base_dir = os.path.dirname(os.path.abspath(__file__))
            weather_db_path = os.path.join(base_dir, 'db/weather.db')
            news_db_path = os.path.join(base_dir, 'db/news.db')

            seoul_tz = pytz.timezone('Asia/Seoul')
            TWELVE_HOURS_AGO = (datetime.now(seoul_tz) - timedelta(hours=12)).strftime('%Y-%m-%d %H:%M:%S')


            conn = sqlite3.connect(news_db_path)
            c = conn.cursor()
            news_count = c.execute("SELECT COUNT(*) FROM news where created_at >= datetime(?)", (TWELVE_HOURS_AGO,)).fetchone()[0]
            conn.close()

            conn = sqlite3.connect(weather_db_path)
            c = conn.cursor()
            weather_count = c.execute("SELECT COUNT(*) FROM weather where created_at >= datetime(?)", (TWELVE_HOURS_AGO,)).fetchone()[0]
            conn.close()
    
            news_ok = news_count >= 30 # 6개 topic * 5개 news
            weather_ok = weather_count >= 1

            return not (news_ok and weather_ok)

        except Exception as e:
            logging.error(f"Error occurred while checking need_to_crawl_when_start: {e}")
            return True