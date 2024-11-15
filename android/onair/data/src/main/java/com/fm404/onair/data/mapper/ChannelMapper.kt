package com.fm404.onair.data.mapper

import com.fm404.onair.data.remote.dto.broadcast.ChannelDto
import com.fm404.onair.data.remote.dto.broadcast.PlaylistItemDto
import com.fm404.onair.domain.model.broadcast.Channel
import com.fm404.onair.domain.model.broadcast.PlaylistItem

fun ChannelDto.toDomain() = Channel(
    uuid = uuid,
    userNickname = userNickname,
    profilePath = profilePath,
    channelName = channelName,
    isDefault = isDefault,
    start = start,
    end = end,
    isEnded = isEnded,
    thumbnail = thumbnail,
    ttsEngine = ttsEngine,
    personality = personality,
    newsTopic = newsTopic,
    playlist = playList.map { it.toDomain() }
)

fun PlaylistItemDto.toDomain() = PlaylistItem(
    title = title,
    artist = artist,
    cover = cover
)