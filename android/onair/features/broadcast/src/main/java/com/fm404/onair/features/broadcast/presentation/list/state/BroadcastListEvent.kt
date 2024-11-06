package com.fm404.onair.features.broadcast.presentation.list.state


sealed interface BroadcastListEvent {
    data object LoadBroadcasts : BroadcastListEvent
    data class OnBroadcastClick(val broadcastId: String) : BroadcastListEvent
    data object OnNotificationClick : BroadcastListEvent
}