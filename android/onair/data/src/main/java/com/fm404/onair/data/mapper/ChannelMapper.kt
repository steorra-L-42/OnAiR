package com.fm404.onair.data.mapper

import com.fm404.onair.data.remote.dto.broadcast.ChannelDto
import com.fm404.onair.domain.model.broadcast.Channel

fun ChannelDto.toChannel() = Channel(
    userNickname = userNickname,
    profilePath = profilePath,
    ttsEngine = tts_engine,
    personality = personality,
    topic = topic,
    channelId = channelId,
    isDefault = isDefault,
    start = start,
    end = end,
    isEnded = isEnded,
    thumbnail = thumbnail
)