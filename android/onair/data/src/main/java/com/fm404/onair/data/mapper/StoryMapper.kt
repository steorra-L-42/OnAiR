package com.fm404.onair.data.mapper

import com.fm404.onair.data.remote.dto.story.CreateStoryRequestDto
import com.fm404.onair.data.remote.dto.story.MusicDto
import com.fm404.onair.data.remote.dto.story.StoryResponseDto
import com.fm404.onair.domain.model.story.CreateStoryRequest
import com.fm404.onair.domain.model.story.Music
import com.fm404.onair.domain.model.story.Story

fun CreateStoryRequest.toDto(): CreateStoryRequestDto = CreateStoryRequestDto(
    title = title,
    content = content,
    music = music?.toDto()
)

fun Music.toDto(): MusicDto = MusicDto(
    musicTitle = musicTitle,
    musicArtist = musicArtist,
    musicCoverUrl = musicCoverUrl
)

fun StoryResponseDto.toDomain(): Story = Story(
    channelUuid = channelUuid,
    storyId = storyId,
    title = title,
    content = content,
    musicList = musicList.map { it.toDomain() }
)

fun MusicDto.toDomain(): Music = Music(
    musicTitle = musicTitle,
    musicArtist = musicArtist,
    musicCoverUrl = musicCoverUrl
)