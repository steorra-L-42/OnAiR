package com.fm404.onair.data.remote.dto.broadcast

data class ChannelListResponse(
    val channelList: List<ChannelListItemDto>
)

data class ChannelListItemDto(
    val userNickname: String,
    val profilePath: String,
    val channelName: String,
    val isDefault: Boolean,
    val start: String,
    val end: String,
    val isEnded: Boolean,
    val thumbnail: String,
    val channelUuid: String,
    val ttsEngine: String,
    val personality: String,
    val newsTopic: String,
    val playList: List<PlaylistItemDto>
)