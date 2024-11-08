package com.fm404.onair.features.broadcast.presentation.detail.state

data class BroadcastDetailState(
    val isPlaying: Boolean = false,
    val broadcastId: String = ""  // 이게 곧 channelName
)