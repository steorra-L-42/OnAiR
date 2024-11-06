package com.fm404.onair.features.broadcast.presentation.create.state

sealed class BroadcastCreateEvent {
    data class OnTitleChange(val title: String) : BroadcastCreateEvent()
    data class OnDescriptionChange(val description: String) : BroadcastCreateEvent()
    object OnCreateClick : BroadcastCreateEvent()
}