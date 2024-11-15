# 환경설정

## 1. 파이썬 설치
```
python3 --version
sudo apt install python3-pip
```

## 2. OPENAI API KEY 설정
```
echo 'export OPENAI_API_KEY="INSERT_YOUR_API_KEY"' >> ~/.bashrc
source ~/.bashrc
echo $OPENAI_API_KEY
```

## 3. 라이브러리 준비
```
python3 -m pip install -r requirements.txt
python3 -m pip install openai==0.28
```

