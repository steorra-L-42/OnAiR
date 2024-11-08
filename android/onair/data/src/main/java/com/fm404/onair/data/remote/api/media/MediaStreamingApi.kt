package com.fm404.onair.data.remote.api.media

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface MediaStreamingApi {
    @GET("channel/{channelName}")
    suspend fun getMediaStream(
        @Path("channelName") channelName: String
    ): Response<ResponseBody>
}