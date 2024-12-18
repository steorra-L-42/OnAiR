package com.fm404.onair.domain.model.story

data class Story(
    val channelUuid: String,
    val storyId: String,
    val title: String,
    val content: String,
    val musicList: List<Music>
)

data class Music(
    val title: String,
    val artist: String,
    val cover: String
)
