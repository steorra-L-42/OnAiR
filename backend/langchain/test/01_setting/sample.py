import json
import openai

response = openai.ChatCompletion.create(
    model="gpt-4o",
    messages=[
        {
            "role": "user",
            "content": "Galaxy S1의 출시일은 언제인가요?"
        },
    ]
)

print(json.dumps(response, indent=2, ensure_ascii=False))
