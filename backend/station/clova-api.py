import os
import sys
import urllib
import urllib2
reload(sys)
sys.setdefaultencoding('utf-8')
client_id = "클라이언트ID여기에"
client_secret = "시크릿키여기에"
text = unicode("반가워요! 24시 온에어 DJ 아린입니다.")
speaker = "vyuna"
speed = "-1" #속도 (숫자가 낮을수록 빠름. vyuna는 -1이 적당함.)
alpha = "1" #음색
volume = "0"
pitch = "1" #높낮이
emotion = "2" #감정 - 기쁨
emotionStrength = "2" # 0약함, 1보통, 2강함
fmt = "mp3"
val = {
    "speaker": speaker,
    "volume": volume,
    "speed":speed,
    "pitch": pitch,
    "text":text,
    "alpha":alpha,
    "emotion":emotion,
    "emotion-strength":emotionStrength,
    "format": fmt
}
data = urllib.urlencode(val)
url = "https://naveropenapi.apigw.ntruss.com/tts-premium/v1/tts"
headers = {
    "X-NCP-APIGW-API-KEY-ID" : client_id,
    "X-NCP-APIGW-API-KEY" : client_secret
}
request = urllib2.Request(url, data, headers)
response = urllib2.urlopen(request)
rescode = response.getcode()
if(rescode==200):
    print("TTS mp3 save")
    response_body = response.read()
    with open('tts_output.mp3', 'wb') as f:
        f.write(response_body)
else:
    print("Error Code:" + rescode)
