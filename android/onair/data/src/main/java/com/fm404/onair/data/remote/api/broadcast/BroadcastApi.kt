package com.fm404.onair.data.remote.api.broadcast

import com.fm404.onair.data.remote.dto.broadcast.ChannelListResponse
import com.fm404.onair.data.remote.dto.broadcast.CreateChannelRequest
import com.fm404.onair.data.remote.dto.broadcast.CreateChannelResponse
import retrofit2.Response
import retrofit2.http.*

interface BroadcastApi {
    @GET("api/v1/channel")
    suspend fun getChannelList(): Response<ChannelListResponse>

    @POST("api/v1/channel")
    suspend fun createChannel(
        @Body request: CreateChannelRequest
    ): Response<CreateChannelResponse>
}