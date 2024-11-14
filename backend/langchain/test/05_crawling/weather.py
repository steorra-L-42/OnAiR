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
text = selenium_wrapper.get_texts_by_css_selector(url, css_selector)
print(text)

selenium_wrapper.close()

########################### 요약1 ###########################
# # https://wikidocs.net/234020

# from langchain.prompts import PromptTemplate
# from langchain_openai import ChatOpenAI
# from langchain.chains.combine_documents import create_stuff_documents_chain
# from langchain_core.output_parsers import JsonOutputParser

# llm = ChatOpenAI(temperature=0, model_name='gpt-4o-mini')

# parser = JsonOutputParser()

# template = f'''
# # TODO
# 당신은 날씨예보를 읽고 주요내용을 요약해야 합니다.
# - summary : 주요내용을 요약한 텍스트

# #Format : 
# - {format_instructions}
# - \`\`\`json, \`\`\`등은 붙이지 않습니다.
# - 최종 -> {{ "summary" : "요약한 내용" }}

# #요약할 내용 :
# {context}
# '''

# prompt = PromptTemplate(template=template, input_variables=['context'])
# prompt = prompt.partial(format_instructions=parser.get_format_instructions())

# result = llm.invoke(prompt.format(context=text))
# answer = result.content
# print(answer)

# import json
# json_data = json.loads(answer)
# print(json_data['summary'])

########################### 요약2 ###########################
# https://wikidocs.net/233789
from langchain_core.prompts import PromptTemplate
from langchain_openai import ChatOpenAI
from langchain_core.output_parsers import JsonOutputParser
from pydantic import BaseModel, Field

llm = ChatOpenAI(temperature=0, model_name='gpt-4o-mini')

class WeatherSummary(BaseModel):
    summary: str = Field(description="날씨 예보를 요약한 내용")


parser = JsonOutputParser(pydantic_object=WeatherSummary)

template = f'''
# TODO
당신은 날씨예보를 읽고 주요내용을 요약해야 합니다.
- summary : 주요내용을 요약한 텍스트

#Format : 
- {{format_instructions}}
- \`\`\`json, \`\`\`등은 붙이지 않습니다.

#요약할 내용 :
{{context}}
'''
prompt = PromptTemplate(template=template, input_variables=['context'])
prompt = prompt.partial(format_instructions=parser.get_format_instructions())

chain = prompt | llm | parser

json_data = chain.invoke({'context': text})
print(json_data)
print(json_data['summary'])

########################### SQLite에 저장 ###########################
import os
import sqlite3

base_dir = os.path.dirname(os.path.abspath(__file__))
db_path = os.path.join(base_dir, 'weather.db')

# 결과 SQLite에 저장
conn = sqlite3.connect(db_path)

c = conn.cursor()

# 테이블 생성
c.execute('''CREATE TABLE IF NOT EXISTS weather (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                summary TEXT
            )''')

# 데이터 삽입
summary = json_data.get('summary', 'No summary found')
c.execute("INSERT INTO weather (summary) VALUES (?)", (summary,))

# 변경사항 저장
conn.commit()

# 연결 종료
conn.close()

########################### SQLite에서 불러오기 ###########################
import sqlite3

# 결과 SQLite에서 조회
conn = sqlite3.connect(db_path) 
conn.row_factory = sqlite3.Row # sqlite3.Row 객체를 사용하여 컬럼명으로 조회 가능

c = conn.cursor()
rows = c.execute("SELECT * FROM weather").fetchall()
for row in rows:
    print(f"내용 : {row['summary']}")

conn.close()