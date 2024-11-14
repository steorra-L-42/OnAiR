package com.fm404.onair.features.broadcast.presentation.create.state

import com.fm404.onair.domain.model.broadcast.CreateChannelPlayList

data class BroadcastCreateState(
    val ttsEngine: String = "",
    val personality: String = "",
    val topic: String = "",
    val playList: List<CreateChannelPlayList> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)