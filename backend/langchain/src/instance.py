from kafka.kafka_producer_wrapper import KafkaProducerWrapper
from ..domain.story.story_service import StoryService
from ..domain.story.story_controller import StoryController
from ..domain.weather.weather_service import WeatherService
from ..domain.weather.weather_controller import WeatherController
from ..domain.news.news_service import NewsService
from ..domain.news.news_controller import NewsController
from ..core.dispatcher import Dispatcher
from ..domain.weather.weather_crawler import WeatherCrawler
from ..domain.news.news_crawler import NewsCrawler
from ..core.scheduler import Scheduler

# 인스턴스 생성 주입 및 관리 클래스
producer = KafkaProducerWrapper()

story_service = StoryService()
story_controller = StoryController(story_service)
weather_service = WeatherService()
weather_controller = WeatherController(weather_service)
news_service = NewsService()
news_controller = NewsController(news_service)
dispatcher = Dispatcher(story_controller, weather_controller, news_controller)

weather_crawler = WeatherCrawler()
news_crawler = NewsCrawler()
scheduler = Scheduler(weather_crawler, news_crawler)