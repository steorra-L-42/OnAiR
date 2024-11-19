package com.fm404.onair.data.remote.dto.story

data class CreateStoryRequestDto(
    val title: String,
    val content: String,
    val music: MusicDto?
)

data class MusicDto(
    val musicTitle: String,
    val musicArtist: String,
    val musicCoverUrl: String
)