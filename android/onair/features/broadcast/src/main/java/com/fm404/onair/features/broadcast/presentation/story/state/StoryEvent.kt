package com.fm404.onair.features.broadcast.presentation.story.state

import com.fm404.onair.domain.model.story.Music

sealed interface StoryEvent {
    data class OnTitleChange(val title: String) : StoryEvent
    data class OnContentChange(val content: String) : StoryEvent
    data class OnMusicSelect(val music: Music) : StoryEvent
    data object OnSubmit : StoryEvent
}