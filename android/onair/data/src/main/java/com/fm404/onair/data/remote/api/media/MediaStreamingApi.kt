package com.fm404.onair.data.remote.api.media

import com.fm404.onair.data.remote.dto.media.ActiveStreamDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface MediaStreamingApi {
    @GET("channel/{channelName}")
    suspend fun getMediaStream(
        @Path("channelName") channelName: String
    ): Response<ResponseBody>

    @GET("streams")
    suspend fun getActiveStreams(): Response<List<ActiveStreamDto>>
}