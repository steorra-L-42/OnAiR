package com.fm404.onair.data.mapper

import com.fm404.onair.data.remote.dto.broadcast.CreateChannelResponse
import com.fm404.onair.data.remote.dto.broadcast.PlayListDto
import com.fm404.onair.domain.model.broadcast.CreateChannelPlayList
import com.fm404.onair.domain.model.broadcast.CreateChannelResult

fun CreateChannelResponse.toCreateChannelResult() = CreateChannelResult(
    userId = userId,
    role = role,
    ttsEngine = tts_engine,
    personality = personality,
    topic = topic,
    playList = playList.map { it.toCreateChannelPlayList() },
    channelId = channelId,
    isDefault = isDefault,
    start = start,
    end = end,
    isEnded = isEnded,
    thumbnail = thumbnail
)

fun PlayListDto.toCreateChannelPlayList() = CreateChannelPlayList(
    musicTitle = playListMusicTitle,
    musicArtist = playListMusicArtist,
    musicCoverUrl = playListMusicCoverUrl
)

fun CreateChannelPlayList.toPlayListDto() = PlayListDto(
    playListMusicTitle = musicTitle,
    playListMusicArtist = musicArtist,
    playListMusicCoverUrl = musicCoverUrl
)