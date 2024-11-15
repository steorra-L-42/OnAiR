package com.fm404.onair.data.mapper

import com.fm404.onair.data.remote.dto.broadcast.ChannelListItemDto
import com.fm404.onair.domain.model.broadcast.ChannelList

fun ChannelListItemDto.toChannelList() = ChannelList(
    channelUuid  = channelUuid,
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