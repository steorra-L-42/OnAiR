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

def chat(title, story, personality, tts_engine):

    chat = ChatOpenAI(
        model="gpt-4o",
    )

    class Answer(BaseModel):
        intro: str = Field(description="사연 읽기 전 사연 소개")
        answer: str = Field(description="사연 읽은 후 답변")
        emotion_tone_preset: str = Field(description="사연을 읽는 감정, 톤")

    parser = OutputFixingParser.from_llm(
        parser=PydanticOutputParser(pydantic_object=Answer),
        llm=chat,
    )

    system_prompt = PromptTemplate(
        template = """
        너는 라디오 DJ다. 
        너는 {personality}한 성격을 가지고 있다.
        - intro: DJ가 사연을 읽기 전에 말하는 안내말이다. 사연의 제목(=title)을 참고한다.
        - story : 시청자의 사연이다. 너는 우선 시청자의 사연(=story)을 라디오 DJ처럼 다른 시청자들에게 읽어준다. 읽으면서 중간중간에 자연스럽게 공감한다.
        - emtion_tone_preset : DJ가 말하는 톤을 나타낸다. 사연의 내용과 DJ의 성격을 참고해 선택한다. {list_emotion_tone_preset} 중 하나를 선택한다
        """,

        input_variables = [
            "personality",
            "list_emotion_tone_preset"
        ]
    )

    human_prompt = PromptTemplate(
        template = """
        최종 요구사항:
        입력되는 personality, title, story를 참고하여 적절한 intro, answer, emtion_tone_preset를 출력한다.
        입력:
        - personality = {personality}
        - title = {title}
        - story = {story}
        출력:
        - intro = DJ가 사연을 읽기 전에 말하는 안내말. 자연스럽게 사연의 제목(=title)을 언급한다.
        - answer = DJ가 답변하는 대사
        - emtion_tone_preset : DJ가 말하는 감정과 톤을 나타낸다. {list_emotion_tone_preset} 중 하나를 선택한다.
        주의사항:
        - intro, answer의 길이는 250자 이내로 출력한다.
        - DJ는 자신의 이름을 언급하지 않는다.
        - 자신의 성격에 맞게 반응한다.
        """,

        input_variables = [
            "personality",
            "title",
            "story",
            "list_emotion_tone_preset",
        ]
    )

    list_emotion_tone_preset=get_list_emotion_tone_preset(tts_engine)

    result = chat.invoke([
        SystemMessage(content=system_prompt.format(personality=personality, list_emotion_tone_preset=list_emotion_tone_preset)),
        HumanMessage(content=human_prompt.format(personality=personality, title=title, story=story, list_emotion_tone_preset=list_emotion_tone_preset)),
        HumanMessage(content=parser.get_format_instructions())
    ])

    parsed_result = parser.parse(result.content)
    logging.info(f"parsed_result : {parsed_result}")

    intro = parsed_result.intro
    answer = parsed_result.answer
    emotion_tone_preset = get_emotion_tone_preset(tts_engine, parsed_result.emotion_tone_preset)

    text = parsed_result.intro + " " + story + ". " + parsed_result.answer
    text = text.replace("\n", " ").replace("\t", " ").replace("    ", " ")

    if len(text) > 600:
        raise ValueError("text length is over 600")
        
    ret = {
        "text": text,
        "emotion_tone_preset": emotion_tone_preset
    }

    return ret