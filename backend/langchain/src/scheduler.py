# TODO: table 생성
# TODO: 부팅 시 크롤링하기
# TODO: 정해진 시간에 크롤링하기
# TODO: 하루 지난 기사, 날씨 정보 삭제
class Scheduler:
    def __init__(self, weather_crawler, news_crawler):
        self.weather_crawler = weather_crawler
        self.news_crawler = news_crawler