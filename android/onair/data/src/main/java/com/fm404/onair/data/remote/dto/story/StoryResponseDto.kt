package com.fm404.onair.data.remote.dto.story

data class StoryResponseDto(
    val channelUuid: String,
    val storyId: String,
    val title: String,
    val content: String,
    val musicList: List<MusicDto>
)