'''
추가 서버의 alertmanager의 훅을 받아 작동하는 지우개입니다.
'''

import os
from fastapi import FastAPI, Request, HTTPException


app = FastAPI()

@app.post("/alert")
def alert():
    data = Request.json
    if not data:
        raise HTTPException(status_code=400, detail="Invalid request payload")
    
    if data.get("alerts"):
        alert_name = data["alerts"][0]["labels"]["alertname"]
        # 서브시스템의 문제인 경우, 이게 서브시스템에서 돌아가고 있기 때문에 여기서 처리할 수 있다.
        if alert_name == "LowAvailableMemory_Sub":
            clear_cache()
        
        # TODO: 메인시스템의 문제인 경우, 추가적인 요청을 보내야한다.
        elif alert_name == "LowAvailableMemory_Main":
            pass

    return {"status": "success"}

def clear_cache():
    os.system("sudo sync; echo 3 | sudo tee /proc/sys/vm/drop_caches")


if __name__=='__main__':
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=5000)