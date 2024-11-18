package com.fm404.onair.data.mapper

import com.fm404.onair.data.remote.dto.broadcast.CreateChannelResponse
import com.fm404.onair.data.remote.dto.broadcast.PlayListDto
import com.fm404.onair.domain.model.broadcast.CreateChannelPlayList
import com.fm404.onair.domain.model.broadcast.CreateChannelResult

fun CreateChannelResponse.toCreateChannelResult() = CreateChannelResult(
    channelUuid = channelUuid,
    channelName = channelName ?: "기본 채널명",
    start = start,
    end = end,
    isDefault = isDefault ?: false,
    ttsEngine = ttsEngine ?: "sena"
)

fun PlayListDto.toCreateChannelPlayList() = CreateChannelPlayList(
    title = title,
    artist = artist,
    cover = cover
)

fun CreateChannelPlayList.toPlayListDto() = PlayListDto(
    title = title,
    artist = artist,
    cover = cover
)