package com.fm404.onair.domain.usecase.media

import com.fm404.onair.domain.model.media.MediaStream
import com.fm404.onair.domain.repository.media.MediaStreamingRepository
import javax.inject.Inject

class GetMediaStreamUseCase @Inject constructor(
    private val repository: MediaStreamingRepository
) {
    suspend operator fun invoke(channelName: String): Result<MediaStream> {
        return repository.getMediaStream(channelName)
    }
}