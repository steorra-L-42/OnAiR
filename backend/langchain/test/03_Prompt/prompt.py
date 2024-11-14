from langchain import PromptTemplate
from langchain.chat_models import ChatOpenAI
from langchain.schema import HumanMessage

chat = ChatOpenAI(
    model_name="gpt-4o",
)

prompt = PromptTemplate(
    template = "{product}는 어느 회사에서 개발한 제품인가요?",

    input_variables = [
        "product"
    ]
)
# JSON 파일로 저장
# prompt_json = prompt.save("prompt.json")
# JSON 파일 불러오기
# loaded_prompt = load_prompt("prompt.json")

result = chat(
    [
        HumanMessage(content=prompt.format(product="GPT-4o")),
    ]
)

print(result.content)
