package com.fm404.onair.data.mapper

import com.fm404.onair.data.remote.dto.media.ActiveStreamDto
import com.fm404.onair.domain.model.media.ActiveStream

fun ActiveStreamDto.toActiveStream() = ActiveStream(
    channelName = channelName,
    viewers = viewers
)