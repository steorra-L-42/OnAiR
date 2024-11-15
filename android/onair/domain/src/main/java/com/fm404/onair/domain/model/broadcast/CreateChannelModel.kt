package com.fm404.onair.domain.model.broadcast

data class CreateChannelPlayList(
    val title: String,
    val artist: String,
    val cover: String
)

data class CreateChannelResult(
    val channelUuid: String,
    val channelName: String,
    val start: String,
    val end: String,
    val isDefault: Boolean,
    val ttsEngine: String
)