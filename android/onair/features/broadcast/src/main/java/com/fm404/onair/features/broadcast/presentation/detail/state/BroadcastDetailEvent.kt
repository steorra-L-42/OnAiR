package com.fm404.onair.features.broadcast.presentation.detail.state

sealed class BroadcastDetailEvent {
    object ToggleStreaming : BroadcastDetailEvent()
}