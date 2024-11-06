package com.fm404.onair.data.mapper

import com.fm404.onair.data.remote.dto.broadcast.BroadcastDto
import com.fm404.onair.domain.model.broadcast.Broadcast

fun BroadcastDto.toBroadcast() = Broadcast(
    id = id,
    title = title,
    description = description,
    startTime = startTime,
    isLive = isLive
)