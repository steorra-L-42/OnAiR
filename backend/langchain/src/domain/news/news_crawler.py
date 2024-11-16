import logging
import requests
import bs4
import os
import sqlite3
from enum import Enum, auto
from langchain_community.document_loaders import WebBaseLoader
from langchain.text_splitter import CharacterTextSplitter
from langchain_core.prompts import PromptTemplate
from langchain_openai import ChatOpenAI
from langchain_core.output_parsers import JsonOutputParser
from pydantic import BaseModel, Field

class NewsTopic(Enum):
    POLITICS = (1, "https://news.naver.com/section/100")
    ECONOMY = (2, "https://news.naver.com/section/101")
    SOCIETY = (3, "https://news.naver.com/section/102")
    LIFE_CULTURE = (4, "https://news.naver.com/section/103")
    WORLD = (5, "https://news.naver.com/section/104")
    IT_SCIENCE = (6, "https://news.naver.com/section/105")

    def __init__(self, id, url):
        self.id = id
        self.url = url

class NewsCrawler:

    CRAWLING_COUNT = 5

    base_dir = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
    db_path = os.path.join(base_dir, 'db/news.db')

    def __init__(self):
        logging.info("NewsCrawler initialized.")
        pass

    def crawl(self):
        logging.info("NewsCrawler crawled.")

        for topic in NewsTopic:
            for news_url in self.get_news_urls(topic.url):
                self.crawl_and_save_news(topic.id, news_url)

    def delete_yesterday_news(self):
        logging.info("NewsCrawler deleted yesterday news.")

        try:    
            conn = sqlite3.connect(self.db_path)
            c = conn.cursor()

            c.execute("DELETE FROM news WHERE created_at < datetime('now', '-1 day')")

            conn.commit()
            conn.close()
            logging.info("Yesterday news deleted.")
        except Exception as e:
            logging.error(f"Error cannot delete yesterday news : {e}")

    def get_news_urls(self, topic_url):
        logging.info("NewsCrawler get news.")
        news_urls = []

        try:
            response = requests.get(topic_url)
            response.raise_for_status()

            soup = bs4.BeautifulSoup(response.text, 'html.parser')
            links = soup.select('.as_headline > ul > li > div > div > div > div > a')
            hrefs = [link.get('href') for link in links if 'href' in link.attrs]

            for href in hrefs:
                logging.info(f"News URL : {href}")

            return hrefs[:self.CRAWLING_COUNT]
        
        except requests.exceptions.RequestException as e:
            logging.error(f"Error fetching news urls : {e}")
            return
        except Exception as e:
            logging.error(f"Error : {e}")
            return news_urls

    def crawl_and_save_news(self,topic_id, news_url):
        docs = None
        try:
            # 웹 문서 크롤링
            loader = WebBaseLoader(
                web_paths=[news_url],
                bs_kwargs=dict(
                    parse_only=bs4.SoupStrainer(
                        'div', 
                        attrs={'class':['newsct_article _article_body']}
                    )
                ),
                header_template={
                    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3',
                }
            )

            # 뉴스기사의 본문을 Chunk 단위로 쪼갬
            text_splitter = CharacterTextSplitter(        
                separator="\n\n",
                chunk_size=3000,     # 쪼개는 글자수
                chunk_overlap=300,   # 오버랩 글자수
                length_function=len,
                is_separator_regex=False,
            )

            # 웹사이트 내용 크롤링 후 Chunk 단위로 분할
            docs = loader.load_and_split(text_splitter)
        except Exception as e:
            logging.error(f"Error failed to crawl news : {e}")
            return

        try:    
            llm = ChatOpenAI(temperature=0, model_name='gpt-4o-mini')

            class NewsSummary(BaseModel):
                title: str = Field(description="title of the news")
                summary: str = Field(description="summary of the news")


            parser = JsonOutputParser(pydantic_object=NewsSummary)

            template = f'''
            # TODO
            당신은 뉴스기사를 읽고 기사의 주요내용을 요약해야 합니다.
            - title : 신문기사의 제목
            - summary : 주요내용을 요약

            #Format : 
            {{format_instructions}}
            \`\`\`json, \`\`\`등은 붙이지 않습니다.

            #요약할 내용 :
            {{context}}
            '''

            prompt = PromptTemplate(template=template, input_variables=['context'])
            prompt = prompt.partial(format_instructions=parser.get_format_instructions())

            chain = prompt | llm | parser

            json_data = chain.invoke({'context': docs})
        except Exception as e:
            logging.error(f"Error failed to summarize news : {e}")
            return

        try:
            # 결과 SQLite에 저장
            conn = sqlite3.connect(self.db_path)

            c = conn.cursor()

            # 테이블 생성
            c.execute('''CREATE TABLE IF NOT EXISTS news (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            title TEXT,
                            summary TEXT,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            topic_id INTEGER
                        )''')

            # 데이터 삽입
            title = json_data['title']
            summary = json_data['summary']
            c.execute("INSERT INTO news (title, summary, topic_id) VALUES (?, ?, ?)", (title, summary, topic_id))

            # 변경사항 저장
            conn.commit()

            # 연결 종료
            conn.close()
        except Exception as e:
            logging.error(f"Error failed to save news : {e}")
            return