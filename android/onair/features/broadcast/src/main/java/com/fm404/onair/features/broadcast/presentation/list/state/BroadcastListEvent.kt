package com.fm404.onair.features.broadcast.presentation.list.state

import com.fm404.onair.domain.model.broadcast.Channel

sealed class BroadcastListEvent {
    object LoadChannels : BroadcastListEvent()
    data class OnChannelClick(val channel: Channel) : BroadcastListEvent()
    object OnNotificationClick : BroadcastListEvent()
}