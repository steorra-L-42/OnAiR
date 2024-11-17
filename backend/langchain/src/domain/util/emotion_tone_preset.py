from enum import Enum

sena_emontion_tone_preset = {
    "보통의": "tonemid-1",
    "차분한": "tonemid-2",
    "다소낮은": "tonemid-3",
    "또랑또랑한": "tonemid-4",
    "공감하듯": "normal-1",
    "정보를전달하듯": "normal-2",
    "설득하듯": "normal-3",
    "나긋나긋한": "normal-4",
    "밝고유쾌한": "happy-1",
    "아주행복한": "happy-2",
    "귀엽고즐거움": "happy-3",
    "약간슬픔": "sad-1",
    "억울한슬픔": "sad-2",
    "절망적인슬픔": "sad-3",
    "화가나는슬픔": "sad-4",
    # "경미한화남": "angry-1",
    # "강한화남": "angry-2",
    # "화가나서따지는톤": "angry-3"
}

jerome_emontion_tone_preset = {
    "보통목소리": "normal-1",
    "또박또박한목소리": "normal-2",
    "빠르게정보를전달하듯": "normal-3",
    "높은목소리": "normal-4"
}

hyunji_emontion_tone_preset = {
    "보통목소리": "normal-1",
    "차분한목소리": "normal-2",
    "낮은목소리": "normal-3",
    "높은목소리": "normal-4"
}

eunbin_emontion_tone_preset = {
    "밝고유쾌한": "happy-1",
    "강한기쁨": "happy-2",
    "부드럽게공감하듯이": "soft-1",
    "무언가를읽듯이": "soft-2",
    "보통의": "normal-1",
    "공감하듯": "normal-2"
}

def get_list_emotion_tone_preset(tts_engine):
    tts_engine = tts_engine.strip()
    if tts_engine == "TYPECAST_SENA":
        return sena_emontion_tone_preset.keys()
    if tts_engine == "TYPECAST_JEROME":
        return jerome_emontion_tone_preset.keys()
    if tts_engine == "TYPECAST_HYEONJI":
        return hyunji_emontion_tone_preset.keys()
    if tts_engine == "TYPECAST_EUNBIN":
        return eunbin_emontion_tone_preset.keys()
    return None

def get_emotion_tone_preset(tts_engine, key):
    tts_engine = tts_engine.strip()
    if tts_engine == "TYPECAST_SENA":
        return sena_emontion_tone_preset[key]
    if tts_engine == "TYPECAST_JEROME":
        return jerome_emontion_tone_preset[key]
    if tts_engine == "TYPECAST_HYEONJI":
        return hyunji_emontion_tone_preset[key]
    if tts_engine == "TYPECAST_EUNBIN":
        return eunbin_emontion_tone_preset[key]
    return None