package com.fm404.onair.data.remote.api.broadcast

import com.fm404.onair.data.remote.dto.broadcast.BroadcastDto
import com.fm404.onair.data.remote.dto.broadcast.ChannelDto
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

    @GET("api/v1/channel/{channelUuid}")
    suspend fun getChannel(@Path("channelUuid") channelUuid: String): Response<ChannelDto>

    @GET("broadcasts")
    suspend fun getBroadcastList(): Response<List<BroadcastDto>>
}