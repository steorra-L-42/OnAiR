package com.fm404.onair.features.broadcast.presentation.list.state

import com.fm404.onair.domain.model.broadcast.Channel

data class BroadcastListState(
    val isLoading: Boolean = false,
    val channels: List<Channel> = emptyList(),
    val error: String? = null
)