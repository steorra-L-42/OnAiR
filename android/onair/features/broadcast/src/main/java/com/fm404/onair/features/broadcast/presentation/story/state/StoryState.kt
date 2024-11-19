package com.fm404.onair.features.broadcast.presentation.story.state

import com.fm404.onair.domain.model.story.Music

data class StoryState(
    val title: String = "",
    val content: String = "",
    val selectedMusic: Music? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)