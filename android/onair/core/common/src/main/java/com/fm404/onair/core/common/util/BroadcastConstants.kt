package com.fm404.onair.core.common.util

object BroadcastConstants {
    // TTS Engine 매핑
    val TTS_ENGINE_OPTIONS = mapOf(
        "TYPECAST_SENA" to "세나",
        "TYPECAST_JEROME" to "제롬",
        "TYPECAST_HYEONJI" to "현지",
        "TYPECAST_EUNBIN" to "은빈"
    )

    // TTS Engine에 따른 썸네일 매핑
    val TTS_THUMBNAIL_MAPPING = mapOf(
        "TYPECAST_SENA" to "sena.webp",
        "TYPECAST_JEROME" to "jerome.webp",
        "TYPECAST_HYEONJI" to "hyunji.webp",
        "TYPECAST_EUNBIN" to "eunbin.webp"
    )

    // 뉴스 주제 매핑
    val NEWS_TOPIC_OPTIONS = mapOf(
        "POLITICS" to "정치",
        "ECONOMY" to "경제",
        "SOCIETY" to "사회",
        "LIFE_CULTURE" to "생활/문화",
        "IT_SCIENCE" to "IT/과학",
        "WORLD" to "세계"
    )

    // 성격 매핑
    val PERSONALITY_OPTIONS = mapOf(
        "BRIGHT" to "밝은",
        "CALM" to "차분한",
        "FRIENDLY" to "친근한",
        "SERIOUS" to "진지한",
        "ANGRY" to "화난",
        "FUNNY" to "재밌는",
        "SEXY" to "섹시한",
        "CUTE" to "귀여운",
        "COOL" to "시원한",
        "WARM" to "따뜻한",
        "COLD" to "차가운",
        "STRONG" to "강한",
        "WEAK" to "약한",
        "SMART" to "똑똑한",
        "STUPID" to "멍청한",
        "EVIL" to "악한",
        "MEAN" to "심술쟁이",
        "GENTLE" to "온순한",
        "ROUGH" to "거친",
        "SMOOTH" to "부드러운"
    )
}