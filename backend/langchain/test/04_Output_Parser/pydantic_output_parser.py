import langchain
# from langchain.cache import SQLiteCache
from langchain_community.cache import SQLiteCache
# from langchain.chat_models import ChatOpenAI
from langchain_openai import ChatOpenAI
from langchain.schema import HumanMessage, SystemMessage
from langchain.output_parsers import OutputFixingParser
from langchain.output_parsers import PydanticOutputParser
from pydantic import BaseModel, Field, field_validator
# from langchain.llms import OpenAI
from langchain_community.llms import OpenAI
from langchain.prompts import PromptTemplate, FewShotPromptTemplate

langchain.llm_cache = SQLiteCache("llm_cache.db")

chat = ChatOpenAI(
    model="gpt-4o",
)

class Answer(BaseModel):

    intro: str = Field(description="안내")
    read: str = Field(description="낭독")
    answer: str = Field(description="답변")
    feeling: str = Field(description="감정")
    speed: int = Field(description="속도")

    # @validator("intro")
    # def intro_must_not_be_null(cls, value):
    #     if value is None:
    #         raise ValueError("intro must not be null")
    #     return value

    # @validator("read")
    # def read_must_not_be_null(cls, value):
    #     if value is None:
    #         raise ValueError("read must not be null")
    #     return value

    # @validator("answer")
    # def answer_must_not_be_null(cls, value):
    #     if value is None:
    #         raise ValueError("answer must not be null")
    #     return value

    # @validator("feeling")
    # def feeling_must_not_be_null(cls, value):
    #     if value is None:
    #         raise ValueError("feeling must not be null")
    #     return value

    # @validator("speed")
    # def speed_must_be_positive(cls, value):
    #     if value < 0:
    #         raise ValueError("speed must be positive")
    #     return value

    @field_validator("intro")
    def intro_must_not_be_null(cls, value):
        if value is None:
            raise ValueError("intro must not be null")
        return value

    @field_validator("read")
    def read_must_not_be_null(cls, value):
        if value is None:
            raise ValueError("read must not be null")
        return value

    @field_validator("answer")
    def answer_must_not_be_null(cls, value):
        if value is None:
            raise ValueError("answer must not be null")
        return value

    @field_validator("feeling")
    def feeling_must_not_be_null(cls, value):
        if value is None:
            raise ValueError("feeling must not be null")
        return value

    @field_validator("speed")
    def speed_must_be_positive(cls, value):
        if value <= 0:
            raise ValueError("speed must be positive")
        return value

parser = OutputFixingParser.from_llm(
    parser=PydanticOutputParser(pydantic_object=Answer),
    llm=chat,
)

class Example:
    def __init__(self, gender, personality, story, intro, read, answer, feeling, speed):
        self.gender = gender
        self.personality = personality
        self.story = story
        self.intro = intro
        self.read = read
        self.answer = answer
        self.feeling = feeling
        self.speed = speed

example = Example(
    gender="여성",
    personality="친절하고 부드러운",
    story="""
    안녕하세요. 저는 구미 사는 20대 여성입니다. 
    저는 중학교 시절부터 친한 친구가 있습니다.
    저와는 다르게 친구는 느긋하고 무뚝뚝한 성격을 가지고 있습니다.
    평소에는 그런 친구의 성격을 이해하고 지내왔지만, 최근 성격 때문에 다투었습니다.
    저는 여행 계획을 철저하게 세우는 편입니다.
    하지만 친구는 짜여진 일정에 맞춰 움직이는 것을 싫어합니다.
    정성들여 준비한 여행 계획을 친구가 무시하고 싶은 대로 움직이자, 저는 화가 났습니다.
    그 친구가 싫은 것은 아니지만 그런 성격 때문에 답답한 마음이 듭니다.
    어떻게 해야 할까요?
    """,
    intro="""
    좋아요, 청취자님의 사연을 먼저 읽어드릴게요.
    구미에서 온 20대 여성 청취자님의 고민 가득한 사연을 들여드리겠습니다.
    """,
    read="""
    안녕하세요. 저는 구미 사는 20대 여성입니다. 저에게는 중학교 시절부터 친한 친구가 있습니다. 
    저와는 다르게 친구는 느긋하고 무뚝뚝한 성격을 가지고 있습니다. 
    평소에는 그런 친구의 성격을 이해하고 지내왔지만, 최근에는 그 성격 때문에 다투게 되었습니다.
    저는 여행 계획을 철저하게 세우는 편이라 미리 여행 일정을 정해놓고 갔습니다. 
    하지만 친구는 짜여진 일정에 맞춰 움직이는 것을 싫어합니다. 
    정성 들여 준비한 여행 계획을 친구가 무시하고 싶은 대로 움직이자, 저는 화가 났습니다. 
    그 친구가 싫은 것은 아니지만, 그런 성격 때문에 답답한 마음이 듭니다.
    어떻게 해야 할까요?
    """,
    answer="""
    사연을 들으니 마음이 참 복잡하셨을 것 같아요. 
    서로 다른 성향 때문에 충돌이 생기면 더 속상하게 느껴지죠. 
    특히 애써 준비한 여행 계획을 친구가 다르게 받아들일 때, 청취자님의 정성이 무시된 것 같아 아쉬운 마음이 드셨을 거예요.
    계획적이고 꼼꼼한 성격의 청취자님께는 여행의 일정이 중요하고, 친구분은 자유롭게 움직이는 걸 더 선호하시니 서로를 이해하는 게 쉽지 않으셨을 거라 생각해요. 
    청취자님의 진심이 잘 전달될 수 있도록 작은 대화를 시도해 보는 건 어떨까요? 
    그러면 친구분도 청취자님의 마음을 이해하고, 다음엔 서로 조금씩 맞춰가며 즐거운 여행을 함께할 수 있을 거예요.
    """,
    feeling="tonemid-1",
    speed="1"
)

system_prompt = PromptTemplate(
    template = """
    너는 라디오 DJ다. 
    너의 gender는 {gender}이고, personality는 {personality}다.
    - intro: DJ가 사연을 읽기 전에 말하는 안내말이다.
    - read : DJ가 시청자의 사연(=story)을 읽는다.
    - story : 시청자의 사연이다. 너는 우선 시청자의 사연(=story)을 라디오 DJ처럼 다른 시청자들에게 읽어준다. 읽으면서 중간중간에 자연스럽게 공감한다.
    - feeling : DJ가 말하는 톤을 나타낸다.
    (선택지 : tonemid-1 : 중간 톤 1, normal-1 : 일반적인 톤 1, happy-1 : 밝고 유쾌한 느낌, happy-2 : 더 강한 행복감, happy-3 : 강한 활기와 즐거움, sad-1 : 약한 슬픔, sad-2 : 깊은 슬픔, sad-3 : 더 깊은 슬픔, sad-4 : 매우 깊고 무거운 슬픔, angry-1 경미한 화남, angry-2 조금 더 강한 화남, angry-3 매우 강한 화남
    - speed : 말하는 속도로 0.5 ~ 1.5 사이 값이다. (1.5: 느림, 1: 보통, 0.5: 빠름)
    """,

    input_variables = [
        "gender",
        "personality"
    ]
)

example_prompt = PromptTemplate(
    template = """
    예시:
    input : gender={example_gender}, personality={example_personality}, story={example_story}
    output : intro={example_intro}, read={example_read}, answer ={example_answer}, feeling={example_feeling}, speed={example_speed}
    """,

    input_variables = [
        "example_gender",
        "example_personality",
        "example_story",
        "example_intro",
        "example_read",
        "example_answer",
        "example_feeling",
        "example_speed"
    ]
)

human_prompt = PromptTemplate(
    template = """
    최종 요구사항:
    입력되는 gender, personality, story를 참고하여 적절한 intro, read, answer, feeling, speed를 출력한다.
    입력:
    - gender = {gender}
    - personality = {personality}
    - story = {story}
    출럭:
    - intro = DJ가 사연을 읽기 전에 말하는 안내말
    - read = DJ가 사연을 읽는 대사
    - answer = DJ가 답변하는 대사
    - feeling = DJ의 감정
    - speed = DJ의 말하는 속도
    """,

    input_variables = [
        "gender",
        "personality",
        "story"
    ]
)

request1 = {
    "gender" : "여성",
    "personality" : "당차고 활기찬",
    "story" : """
    안녕하세요, 저는 서울에 사는 30대 직장인 남성입니다. 
    제게는 대학 시절부터 친하게 지내온 친구가 있습니다. 
    성격도, 관심사도 비슷해서 그 친구와 함께 보내는 시간이 항상 즐거웠죠. 
    그런데 얼마 전, 그 친구가 결혼을 하게 되었습니다.
    결혼을 하고 나서부터 그 친구와 만나는 시간이 줄어들었습니다.
    놀러가는 것도, 연락하는 것도 어려워졌어요.
    같이 놀고 대화할 친구가 없어져 많이 외롭고 허전한 기분이 들어요.
    저도 결혼해야 하는 걸까요?
    """
}

# result = chat([
#     SystemMessage(content=system_prompt.format(gender=request1["gender"], personality=request1["personality"])),
#     SystemMessage(content=example_prompt.format(
#         example_gender=example.gender, example_personality=example.personality, 
#         example_story=example.story, example_intro=example.intro,
#         example_read=example.read, example_answer=example.answer, 
#         example_feeling=example.feeling, example_speed=example.speed)),
#     HumanMessage(content=human_prompt.format(gender=request1["gender"], personality=request1["personality"], story=request1["story"])),
#     HumanMessage(content=parser.get_format_instructions())
# ])

result = chat.invoke([
    SystemMessage(content=system_prompt.format(gender=request1["gender"], personality=request1["personality"])),
    SystemMessage(content=example_prompt.format(
        example_gender=example.gender, example_personality=example.personality, 
        example_story=example.story, example_intro=example.intro,
        example_read=example.read, example_answer=example.answer, 
        example_feeling=example.feeling, example_speed=example.speed)),
    HumanMessage(content=human_prompt.format(gender=request1["gender"], personality=request1["personality"], story=request1["story"])),
    HumanMessage(content=parser.get_format_instructions())
])

# result = chat.invoke([
#     SystemMessage(content=system_prompt.format(gender=request1["gender"], personality=request1["personality"])),
#     SystemMessage(content=example_prompt.format(
#         example_gender=example.gender, example_personality=example.personality, 
#         example_story=example.story, example_intro=example.intro,
#         example_read=example.read, example_answer=example.answer, 
#         example_feeling=example.feeling, example_speed=example.speed)),
#     HumanMessage(content=human_prompt.format(gender=request1["gender"], personality=request1["personality"], story=request1["story"])),
#     HumanMessage(content=parser.get_format_instructions())
# ])

parsed_result = parser.parse(result.content)

print(f"안내 : {parsed_result.intro}")
print(f"낭독 : {parsed_result.read}")
print(f"답변 : {parsed_result.answer}")
print(f"감정 : {parsed_result.feeling}")
print(f"속도 : {parsed_result.speed}")

# result.content를 json 형태로 저장
# import json
# with open("result.json", "w") as f:
#     json.dump(result.content, f)

# result.content를 json으로 변환
import json
json_result = json.dumps(result.content, indent=4, ensure_ascii=False)
print(json_result)


# json parser
# https://wikidocs.net/233789