# 정치
# https://news.naver.com/section/100
# 경제
# https://news.naver.com/section/101
# 사회
# https://news.naver.com/section/102
# 생활/문화
# https://news.naver.com/section/103
# 세계
# https://news.naver.com/section/104
# IT/과학
# https://news.naver.com/section/105
# langchain tutorial
# https://teddylee777.github.io/langchain/langchain-tutorial-05/

# from langchain.chat_models import ChatOpenAI
# from langchain.document_loaders import WebBaseLoader

########################### 크롤링 ###########################

# 네이버 뉴스기사 주소
# url = 'https://n.news.naver.com/mnews/article/214/0001386157'
url = 'https://n.news.naver.com/mnews/article/005/0001738264'

import bs4
from langchain_community.document_loaders import WebBaseLoader
from langchain.text_splitter import CharacterTextSplitter

# 웹 문서 크롤링
loader = WebBaseLoader(
    web_paths=[url],
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
print(docs)

########################### 요약1 ###########################
# https://teddylee777.github.io/langchain/langchain-tutorial-05/

# from langchain_openai import ChatOpenAI
# from langchain.chains.summarize import load_summarize_chain
# from langchain.prompts import PromptTemplate

# # 각 Chunk 단위의 템플릿
# template = '''다음의 내용을 한글로 요약해줘:

# {text}
# '''

# # 전체 문서(혹은 전체 Chunk)에 대한 지시(instruct) 정의
# combine_template = '''{text}

# 요약의 결과는 다음의 형식으로 작성해줘:
# 제목: 신문기사의 제목
# 주요내용: 한 줄로 요약된 내용
# 작성자: 김철수 대리
# 내용: 주요내용을 불렛포인트 형식으로 작성
# '''

# # 템플릿 생성
# prompt = PromptTemplate(template=template, input_variables=['text'])
# combine_prompt = PromptTemplate(template=combine_template, input_variables=['text'])

# # LLM 객체 생성
# llm = ChatOpenAI(temperature=0, 
#                  model_name='gpt-4o-mini')

# # 요약을 도와주는 load_summarize_chain
# chain = load_summarize_chain(llm, 
#                              map_prompt=prompt, 
#                              combine_prompt=combine_prompt, 
#                              chain_type="map_reduce", 
#                              verbose=False)

# try:
#     summary = chain.invoke(docs)
#     print(summary)
# except TypeError as e:
#     print(f"Error: {e}")

########################### 요약2 ###########################

# # https://wikidocs.net/234020

# from langchain_core.output_parsers import StrOutputParser
# from langchain import hub
# from langchain_openai import ChatOpenAI
# from langchain_core.output_parsers import StrOutputParser

# # map llm 생성
# map_llm = ChatOpenAI(
#     temperature=0,
#     model_name="gpt-4o-mini",
# )

# # map chain 생성
# map_summary = hub.pull("teddynote/map-summary-prompt")

# # map chain 생성
# map_chain = map_summary | map_llm | StrOutputParser()

# # 문서에 대한 주요내용 추출
# doc_summaries = map_chain.batch(docs)

# # 요약된 문서의 수 출력
# print(len(doc_summaries))

# # 일부 문서의 요약 출력
# print(doc_summaries[0])

# # reduce prompt 다운로드
# reduce_prompt = hub.pull("teddynote/reduce-prompt")

# # reduce chain 생성
# reduce_chain = reduce_prompt | map_llm | StrOutputParser()

# answer = reduce_chain.stream({"doc_summaries": doc_summaries, "language": "Korean"})
# print(answer)

########################### 요약3 ###########################
# https://wikidocs.net/234020

# from langchain.prompts import PromptTemplate
# from langchain_openai import ChatOpenAI
# from langchain.chains.combine_documents import create_stuff_documents_chain
# from langchain_core.output_parsers import JsonOutputParser

# llm = ChatOpenAI(temperature=0, model_name='gpt-4o-mini')

# parser = JsonOutputParser()

# template = '''
# # TODO
# 당신은 뉴스기사를 읽고 기사의 주요내용을 요약해야 합니다.
# - title : 신문기사의 제목
# - summary : 주요내용을 요약

# #Format : 
# {format_instructions}
# \`\`\`json, \`\`\`등은 붙이지 않습니다.

# #요약할 내용 :
# {context}
# '''

# prompt = PromptTemplate(template=template, input_variables=['context'])
# prompt = prompt.partial(format_instructions=parser.get_format_instructions())

# stuff_chain = create_stuff_documents_chain(llm, prompt)
# answer = stuff_chain.invoke({'context': docs})
# print(answer)

# import json
# # json_data = json.loads(cleaned_answer)
# json_data = json.loads(answer)
# print(json_data['title'])
# print(json_data['summary'])

########################### 요약 4 ###########################
# https://wikidocs.net/233789
from langchain_core.prompts import PromptTemplate
from langchain_openai import ChatOpenAI
from langchain_core.output_parsers import JsonOutputParser
from pydantic import BaseModel, Field

llm = ChatOpenAI(temperature=0, model_name='gpt-4o-mini')

class NewsSummary(BaseModel):
    title: str = Field(description="기사의 제목")
    summary: str = Field(description="기사의 요약")


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
# print(json_data)
# print(json_data['title'])
# print(json_data['summary'])

########################### SQLite에 저장 ###########################
import os
import sqlite3

base_dir = os.path.dirname(os.path.abspath(__file__))
db_path = os.path.join(base_dir, 'news.db')

# 결과 SQLite에 저장
conn = sqlite3.connect(db_path)

c = conn.cursor()

# 테이블 생성
c.execute('''CREATE TABLE IF NOT EXISTS news (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT,
                summary TEXT
            )''')

# 데이터 삽입
title = json_data['title']
summary = json_data['summary']
c.execute("INSERT INTO news (title, summary) VALUES (?, ?)", (title, summary))

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
rows = c.execute("SELECT * FROM news").fetchall()
for row in rows:
    print(f"제목 : {row['title']}")
    print(f"내용 : {row['summary']}")

conn.close()