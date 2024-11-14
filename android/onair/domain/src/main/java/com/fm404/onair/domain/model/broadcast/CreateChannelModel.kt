package com.fm404.onair.domain.model.broadcast

data class CreateChannelPlayList(
    val musicTitle: String,
    val musicArtist: String,
    val musicCoverUrl: String
)

data class CreateChannelResult(
    val userId: String,
    val role: String,
    val ttsEngine: String,
    val personality: String,
    val topic: String,
    val playList: List<CreateChannelPlayList>,
    val channelId: String,
    val isDefault: Boolean,
    val start: String,
    val end: String,
    val isEnded: Boolean,
    val thumbnail: String
)