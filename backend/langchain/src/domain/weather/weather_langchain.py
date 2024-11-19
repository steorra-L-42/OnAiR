import logging
import langchain
from langchain_core.prompts import PromptTemplate
from langchain_openai import ChatOpenAI
from pydantic import BaseModel, Field

def chat(weather, personality):

    llm = ChatOpenAI(temperature=0, model_name='gpt-4o')

    prompt = PromptTemplate(
        template = '''
        - 설명
        당신은 라디오 DJ다.
        당신은 {personality}한 성격을 가지고 있다.

        - 출력
        당신은 주어진 날씨예보를 읽고 주요내용을 요약해서 시청자에게 전달해야 합니다.
        중간 중간 맥락을 크게 벗어나지 않는 선에서 스몰토크를 하며 요약해봅시다.

        - 요약할 날씨예보 :
        {context}

        - 주의사항
        자신의 이름을 언급하지 않습니다.
        자신의 성격에 맞게 요약해주세요.
        요약한 내용은 600자 이내로 출력합니다.
        ''',

        input_variables=[
            "personality",
            'context'
        ]
    )

    chain = prompt | llm

    result = chain.invoke({'context': weather, 'personality': personality})

    text = result.content.replace("\n", " ").replace("\t", " ").replace("    ", " ")

    if len(text) > 600:
        raise Exception('Text length is over 600')

    ret = {
        'text': text,
        'emotion_tone_preset': 'normal-1'
    }

    return ret