package com.fm404.onair.domain.model.broadcast

data class ChannelList(
    val channelUuid: String,
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
    val playlist: List<PlaylistItem>
)