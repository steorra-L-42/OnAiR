package com.fm404.onair.data.remote.api.broadcast

import com.fm404.onair.data.remote.dto.broadcast.ChannelListResponse
import com.fm404.onair.data.remote.dto.broadcast.CreateBroadcastRequest
import retrofit2.Response
import retrofit2.http.*

interface BroadcastApi {
    @GET("api/v1/channel")
    suspend fun getChannelList(): Response<ChannelListResponse>
}