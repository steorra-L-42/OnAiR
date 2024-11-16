import logging
import os
import sqlite3
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
import time

def init_selenium():
    chrome_options = Options()
    chrome_options.add_argument("--headless")  # 헤드리스 모드
    chrome_options.add_argument("--no-sandbox")
    chrome_options.add_argument("--disable-dev-shm-usage")
    chrome_options.add_argument("--disable-gpu")  # GPU 사용 안 함
    chrome_options.add_argument("--remote-debugging-port=9222")  # 원격 디버깅 포트
    service = Service('/usr/bin/chromedriver')  # chromium 경로 지정
    driver = webdriver.Chrome(service=service, options=chrome_options)
    return driver

class SeleniumWrapper:
    def __init__(self):
        self.driver = init_selenium()

    def get_texts_by_css_selector(self, url, css_selector, wait_time=2):
        self.driver.get(url)
        time.sleep(wait_time)  # 페이지 로드 대기 시간
        elements = self.driver.find_elements(By.CSS_SELECTOR, css_selector)  # CSS 셀렉터로 요소 선택
        texts = [element.text for element in elements if element.text]  # 각 요소의 텍스트 추출
        return texts

    def close(self):
        self.driver.quit()

class WeatherCrawler:

    URL = "https://www.weather.go.kr/w/weather/commentary.do#view4225"
    base_dir = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
    db_path = os.path.join(base_dir, 'db/weather.db')

    def __init__(self):
        logging.info('WeatherCrawler initialized.')
        self.driver = init_selenium()
    
    def crawl(self):
        logging.info('WeatherCrawler crawled.')

        text = None
        try:
            selenium_wrapper = SeleniumWrapper()

            css_selector = "div.right-con div.text-area div.paragraph p.txt"
            text = selenium_wrapper.get_texts_by_css_selector(self.URL, css_selector)

            selenium_wrapper.close()
        except Exception as e:
            logging.error(f"Error cannot crawl weather : {e}")
        
        try:
            # 결과 SQLite에 저장
            conn = sqlite3.connect(self.db_path)

            c = conn.cursor()

            # 테이블 생성
            c.execute('''CREATE TABLE IF NOT EXISTS weather (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            content TEXT,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                        )''')

            # 데이터 삽입
            c.execute("INSERT INTO weather (content) VALUES (?)", (text))

            # 변경사항 저장
            conn.commit()

            # 연결 종료
            conn.close()
            logging.info("Weather crawled and saved.")
        except Exception as e:
            logging.error(f"Error failed to save news : {e}")
            return

    def delete_yesterday_weather(self):
        logging.info('WeatherCrawler deleted yesterday weather.')

        try:
            conn = sqlite3.connect(self.db_path)
            c = conn.cursor()

            c.execute("DELETE FROM weather WHERE created_at < datetime('now', '-1 day')")

            conn.commit()
            conn.close()
            logging.info("Yesterday weather deleted.")
        except Exception as e:
            logging.error(f"Error cannot delete yesterday weather : {e}")