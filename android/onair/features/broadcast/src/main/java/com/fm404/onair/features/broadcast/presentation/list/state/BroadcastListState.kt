package com.fm404.onair.features.broadcast.presentation.list.state

import com.fm404.onair.domain.model.broadcast.Channel
import com.fm404.onair.domain.model.broadcast.Broadcast

data class BroadcastListState(
    val isLoading: Boolean = false,
    val broadcasts: List<Broadcast> = emptyList(),
    val channels: List<Channel> = emptyList(),
    val error: String? = null
)