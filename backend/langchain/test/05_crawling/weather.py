# 기상방송
# 5시, 17시 업데이트
# https://www.weather.go.kr/w/weather/commentary.do#view4225
# https://wikidocs.net/253713

########################## Selenium ##########################
from langchain.chains import LLMChain
from langchain.prompts import PromptTemplate, FewShotPromptTemplate
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

# driver = init_selenium()
# driver.get("https://www.weather.go.kr/w/weather/commentary.do#view4225")
# time.sleep(3)  # 페이지 로드 대기 시간
# print(driver.page_source)  # 페이지 소스 출력
# driver.quit()

# CSS 셀렉터로 복잡한 방식의 요소 선택
class SeleniumWrapper:
    def __init__(self):
        self.driver = init_selenium()

    def get_texts_by_css_selector(self, url, css_selector, wait_time=3):
        self.driver.get(url)
        time.sleep(wait_time)  # 페이지 로드 대기 시간
        elements = self.driver.find_elements(By.CSS_SELECTOR, css_selector)  # CSS 셀렉터로 요소 선택
        texts = [element.text for element in elements if element.text]  # 각 요소의 텍스트 추출
        return texts

    def close(self):
        self.driver.quit()

# LangChain과의 연동
selenium_wrapper = SeleniumWrapper()

# URL과 CSS 셀렉터 설정
url = "https://www.weather.go.kr/w/weather/commentary.do#view4225"
css_selector = "div.right-con div.text-area div.paragraph p.txt"  # 부모 클래스 아래 자식 클래스 p 태그 선택
text_contents = selenium_wrapper.get_texts_by_css_selector(url, css_selector)
print(text_contents)

# # LangChain 설정 및 응답 생성
# combined_text = "\n".join(text_contents)
# template = "Extracted content from the specific sections:\n\n{combined_text}\n\nSummarize it:"
# prompt = PromptTemplate(template=template, input_variables=["combined_text"])
# chain = LLMChain(llm=None, prompt=prompt)  # None을 사용 중인 LLM으로 대체

# # 텍스트 내용을 LangChain으로 처리
# output = chain.run(combined_text=combined_text)
# print(output)

# 드라이버 종료
selenium_wrapper.close()

