'''
추가 서버의 alertmanager의 훅을 받아 작동하는 지우개입니다.
'''
import os
from fastapi import FastAPI, Request, HTTPException
import json

app = FastAPI()

@app.post("/alert")
async def alert(request: Request):
    try:
        data = json.loads(await request.body())
        
        if not data:
            raise HTTPException(status_code=400, detail="Invalid request payload")
        
        if data.get("alerts"):
            alert_name = data["alerts"][0]["labels"]["alertname"]
            
            # 서브시스템의 문제 처리
            if alert_name == "LowAvailableMemory_Sub":
                clear_cache("sub")
            
            # 메인 시스템의 문제 처리
            elif alert_name == "LowAvailableMemory_Main":
                clear_cache("main")

        return {"status": "success"}

    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Internal Server Error: {e}")


def clear_cache(server):
    if server == "sub":
        SUB_SERVER = "sub_key.pem"
        os.system(f"ssh -o StrictHostKeyChecking=no -i {SUB_SERVER} ubuntu@ele.on-air.me 'echo 3 | sudo tee /proc/sys/vm/drop_caches'")

    elif server == "main":
        MAIN_SERVER = "main_key.pem"
        os.system(f"ssh -o StrictHostKeyChecking=no -i {MAIN_SERVER} ubuntu@wonyoung.on-air.me 'echo 3 | sudo tee /proc/sys/vm/drop_caches'")

    print(server, "정리완료")


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=5000)