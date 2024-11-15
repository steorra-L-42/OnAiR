package com.fm404.onair.domain.model.story

data class CreateStoryRequest(
    val title: String,
    val content: String,
    val music: Music?
)