package com.fm404.onair.data.remote.dto.broadcast

data class ChannelListResponse(
    val channel_list: List<ChannelDto>
)

data class ChannelDto(
    val userNickname: String,
    val profilePath: String,
    val tts_engine: String,
    val personality: String,
    val topic: String,
    val channelId: String,
    val isDefault: Boolean,
    val start: String,
    val end: String,
    val isEnded: Boolean,
    val thumbnail: String
)