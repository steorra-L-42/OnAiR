import logging
import langchain
from langchain_openai import ChatOpenAI
from langchain.schema import HumanMessage, SystemMessage
from langchain.output_parsers import OutputFixingParser
from langchain.output_parsers import PydanticOutputParser
from pydantic import BaseModel, Field, field_validator
from langchain_community.llms import OpenAI
from langchain.prompts import PromptTemplate, FewShotPromptTemplate

import sys
import os
sys.path.append(os.path.join(os.path.dirname(os.path.abspath(os.path.dirname(__file__))), "util"))
from emotion_tone_preset import get_list_emotion_tone_preset, get_emotion_tone_preset

def chat(title, news, personality, tts_engine):

    chat = ChatOpenAI(
        model="gpt-4o",
    )

    class Script(BaseModel):
        intro: str = Field(description="a brief introduction to the news before reading it")
        script: str = Field(description="a script for reading the news")
        emotion_tone_preset: str = Field(description="the emotion and tone of voice reading the news")
    
    parser = OutputFixingParser.from_llm(
        parser=PydanticOutputParser(pydantic_object=Script),
        llm=chat,
    )

    system_prompt = PromptTemplate(
        template = """
        너는 라디오 DJ다.
        너는 {personality}한 성격을 가지고 있다.
        - intro: DJ가 뉴스를 읽기 전에 말하는 안내말이다. 뉴스의 제목(=title)을 참고한다.
        - news : DJ가 참고할 뉴스 내용. 시청자에게 전달한 뉴스 내용이다.
        - emtion_tone_preset : DJ가 말하는 톤을 나타낸다. {list_emotion_tone_preset} 중 하나를 선택한다
        """,

        input_variables = [
            "personality",
            "list_emotion_tone_preset"
        ]
    )

    human_prompt = PromptTemplate(
        template = """
        최종 요구사항:
        입력되는 personality, title, news를 참고하여 적절한 intro, script, emtion_tone_preset를 출력한다.

        입력:
        - personality = {personality}
        - title = {title}
        - news = {news}

        출력:
        - intro = DJ가 뉴스를 읽기 전에 말하는 안내말. 자연스럽게 뉴스의 제목(=title)을 언급한다.
        - script = DJ가 시청자에게 말할 대사. 너는 라디오 DJ처럼 시청자들에게 뉴스를 읽어준다. 읽으면서 중간중간에 자연스럽게 리액션한다.
        - emtion_tone_preset : DJ가 말하는 감정과 톤을 나타낸다. 뉴스 내용과 DJ의 성격을 참고해 선택한다. {list_emotion_tone_preset} 중 하나를 선택한다.

        주의사항:
        - intro, script의 길이는 600자 이내로 출력한다.
        - DJ는 자신의 이름을 언급하지 않는다.
        - 자신의 성격에 맞게 반응한다.
        """,

        input_variables = [
            "personality",
            "title",
            "news",
            "list_emotion_tone_preset",
        ]
    )

    list_emotion_tone_preset=get_list_emotion_tone_preset(tts_engine)

    result = chat.invoke([
        SystemMessage(content=system_prompt.format(personality=personality, list_emotion_tone_preset=list_emotion_tone_preset)),
        HumanMessage(content=human_prompt.format(personality=personality, title=title, news=news, list_emotion_tone_preset=list_emotion_tone_preset)),
        HumanMessage(content=parser.get_format_instructions())
    ])

    parsed_result = parser.parse(result.content)
    logging.info(f"parsed_result : {parsed_result}")

    intro = parsed_result.intro
    script = parsed_result.script
    emotion_tone_preset = get_emotion_tone_preset(tts_engine, parsed_result.emotion_tone_preset)

    text = parsed_result.intro + " " + parsed_result.script
    text = text.replace("\n", " ").replace("\t", " ").replace("    ", " ")

    if len(text) > 600:
        raise Exception('Text length is over 600')
    
    ret = {
        'text': text,
        'emotion_tone_preset': emotion_tone_preset
    }

    return ret