package com.fm404.onair.features.broadcast.presentation.create.state

import com.fm404.onair.domain.model.broadcast.CreateChannelPlayList

data class BroadcastCreateState(
    val ttsEngine: String = "TYPECAST_SENA",
    val personality: String = "GENTLE",
    val newsTopic: String = "",
    val thumbnail: String = "",
    val channelName: String = "",
    val trackList: List<CreateChannelPlayList> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)