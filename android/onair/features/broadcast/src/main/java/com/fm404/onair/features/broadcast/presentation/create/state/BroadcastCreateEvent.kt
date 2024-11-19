package com.fm404.onair.features.broadcast.presentation.create.state

import com.fm404.onair.domain.model.broadcast.CreateChannelPlayList

sealed class BroadcastCreateEvent {
    data class OnTtsEngineChange(val ttsEngine: String) : BroadcastCreateEvent()
    data class OnPersonalityChange(val personality: String) : BroadcastCreateEvent()
    data class OnNewsTopicChange(val newsTopic: String) : BroadcastCreateEvent()
    data class OnThumbnailChange(val thumbnail: String) : BroadcastCreateEvent()
    data class OnChannelNameChange(val channelName: String) : BroadcastCreateEvent()
    data class OnTrackListChange(val trackList: List<CreateChannelPlayList>) : BroadcastCreateEvent()
    object OnCreateClick : BroadcastCreateEvent()
}