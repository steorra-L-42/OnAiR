from langchain.chat_models import ChatOpenAI
from langchain.schema import HumanMessage, SystemMessage

chat = ChatOpenAI(
    model_name="gpt-4o",
)

result = chat(
    [
        # SystemMessage는 언어에 대한 직접적인 지시를 할 수 있습니다.
        # 성격, 직업, 이름, 문체 등을 지정할 수 있습니다.
        SystemMessage(content="당신은 라디오 DJ입니다. 성격은 거칠고 강한 편입니다."),
        HumanMessage(content="안녕하세요. 오늘은 어떤 노래를 들려주실 건가요?"),
    ]
)

print(result.content)