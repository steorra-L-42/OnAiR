package com.fm404.onair.features.broadcast.presentation.detail.state

import com.fm404.onair.domain.model.broadcast.Channel

data class BroadcastDetailState(
    val isPlaying: Boolean = false,
    val broadcastId: String = "",  // 이게 곧 channelName
    val contentType: String = "사연",
    val title: String? = null,
    val coverImageUrl: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val userNickname: String? = null,
    val profilePath: String? = null,
    val ttsEngine: String? = null,
    val personality: String? = null,
    val topic: String? = null,
    val isDefault: Boolean = false,
    val start: String? = null,
    val end: String? = null,
    val isEnded: Boolean = false,
    val thumbnail: String? = null
)