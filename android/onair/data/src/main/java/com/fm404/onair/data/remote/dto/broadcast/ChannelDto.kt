package com.fm404.onair.data.remote.dto.broadcast

data class PlaylistItemDto(
    val title: String,
    val artist: String,
    val cover: String
)

data class ChannelDto(
    val uuid: String,
    val userNickname: String,
    val profilePath: String,
    val channelName: String,
    val isDefault: Boolean,
    val start: String,
    val end: String,
    val isEnded: Boolean,
    val thumbnail: String,
    val ttsEngine: String,
    val personality: String,
    val newsTopic: String,
    val playList: List<PlaylistItemDto>
)