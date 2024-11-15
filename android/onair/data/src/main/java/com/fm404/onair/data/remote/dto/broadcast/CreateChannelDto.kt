package com.fm404.onair.data.remote.dto.broadcast

data class CreateChannelRequest(
    val ttsEngine: String,
    val personality: String,
    val newsTopic: String,
    val thumbnail: String,
    val channelName: String,
    val trackList: List<PlayListDto>
)

data class PlayListDto(
    val title: String,
    val artist: String,
    val cover: String
)

data class CreateChannelResponse(
    val channelUuid: String,
    val channelName: String,
    val start: String,
    val end: String,
    val isDefault: Boolean,
    val ttsEngine: String
)