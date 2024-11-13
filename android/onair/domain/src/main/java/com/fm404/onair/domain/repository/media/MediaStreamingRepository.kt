package com.fm404.onair.domain.repository.media

import com.fm404.onair.domain.model.media.ActiveStream
import com.fm404.onair.domain.model.media.MediaStream

interface MediaStreamingRepository {
    suspend fun getMediaStream(channelName: String): Result<MediaStream>
    suspend fun getActiveStreams(): Result<List<ActiveStream>>
}