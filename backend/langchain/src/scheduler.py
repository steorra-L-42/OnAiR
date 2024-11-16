import schedule
import time

class Scheduler:

    MORNING = "06:00"
    EVENING = "18:00"
    MIDNIGHT = "00:00"

    def __init__(self, weather_crawler, news_crawler):
        self.weather_crawler = weather_crawler
        self.news_crawler = news_crawler

    def start(self):
        # 부팅 시 크롤링
        self.weather_crawler.crawl()
        self.news_crawler.crawl()

        # weather
        schedule.every().day.at(self.MORNING).do(self.weather_crawler.crawl)
        schedule.every().day.at(self.EVENING).do(self.weather_crawler.crawl)

        # news
        schedule.every().day.at(self.MORNING).do(self.news_crawler.crawl)
        schedule.every().day.at(self.EVENING).do(self.news_crawler.crawl)
    
        # 하루 지난 기사, 날씨 정보 삭제
        schedule.every().day.at(self.MIDNIGHT).do(self.news_crawler.delete_yesterday_news)
        schedule.every().day.at(self.MIDNIGHT).do(self.weather_crawler.delete_yesterday_weather)

        while True:
            schedule.run_pending()
            time.sleep(55) # 55초마다 반복


