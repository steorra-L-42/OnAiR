package com.fm404.onair.domain.model.broadcast

data class Channel(
    val userNickname: String,
    val profilePath: String,
    val ttsEngine: String,
    val personality: String,
    val topic: String,
    val channelId: String,
    val isDefault: Boolean,
    val start: String,
    val end: String,
    val isEnded: Boolean,
    val thumbnail: String
)