package com.fm404.onair.features.broadcast.presentation.create.state

import com.fm404.onair.domain.model.broadcast.CreateChannelPlayList

sealed class BroadcastCreateEvent {
    data class OnTtsEngineChange(val ttsEngine: String) : BroadcastCreateEvent()
    data class OnPersonalityChange(val personality: String) : BroadcastCreateEvent()
    data class OnTopicChange(val topic: String) : BroadcastCreateEvent()
    data class OnPlayListChange(val playList: List<CreateChannelPlayList>) : BroadcastCreateEvent()
    object OnCreateClick : BroadcastCreateEvent()
}