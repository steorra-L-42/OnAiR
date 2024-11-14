package com.fm404.onair.data.repository.media

import com.fm404.onair.data.mapper.toActiveStream
import com.fm404.onair.data.remote.api.media.MediaStreamingApi
import com.fm404.onair.domain.model.media.ActiveStream
import com.fm404.onair.domain.model.media.MediaStream
import com.fm404.onair.domain.repository.media.MediaStreamingRepository
import javax.inject.Inject

class MediaStreamingRepositoryImpl @Inject constructor(
    private val mediaStreamingApi: MediaStreamingApi
) : MediaStreamingRepository {
    override suspend fun getMediaStream(channelName: String): Result<MediaStream> {
        return try {
            val response = mediaStreamingApi.getMediaStream(channelName)
            if (response.isSuccessful) {
                val mediaStream = MediaStream(
                    url = response.raw().request.url.toString()
                )
                Result.success(mediaStream)
            } else {
                Result.failure(Exception("Failed to fetch media stream"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getActiveStreams(): Result<List<ActiveStream>> = runCatching {
        val response = mediaStreamingApi.getActiveStreams()
        if (response.isSuccessful) {
            response.body()?.map { it.toActiveStream() } ?: emptyList()
        } else {
            throw Exception(response.message())
        }
    }
}