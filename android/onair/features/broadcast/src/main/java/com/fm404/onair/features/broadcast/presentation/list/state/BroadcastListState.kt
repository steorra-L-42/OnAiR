package com.fm404.onair.features.broadcast.presentation.list.state

import com.fm404.onair.domain.model.broadcast.Broadcast
import com.fm404.onair.domain.model.broadcast.ChannelList

data class BroadcastListState(
    val isLoading: Boolean = false,
    val broadcasts: List<Broadcast> = emptyList(),
    val channels: List<ChannelList> = emptyList(),
    val error: String? = null
)