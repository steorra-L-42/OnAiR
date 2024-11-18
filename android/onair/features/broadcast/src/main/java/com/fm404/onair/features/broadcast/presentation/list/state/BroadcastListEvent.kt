package com.fm404.onair.features.broadcast.presentation.list.state

import com.fm404.onair.domain.model.broadcast.Channel

sealed interface BroadcastListEvent {
    data object LoadBroadcasts : BroadcastListEvent
    data object LoadChannels : BroadcastListEvent
    data class OnBroadcastClick(val broadcastId: String) : BroadcastListEvent
    data class OnChannelClick(val channelUuid: String) : BroadcastListEvent
    data object OnNotificationClick : BroadcastListEvent
}