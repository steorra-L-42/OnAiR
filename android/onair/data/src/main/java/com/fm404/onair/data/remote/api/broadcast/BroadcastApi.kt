package com.fm404.onair.data.remote.api.broadcast

import com.fm404.onair.data.remote.dto.broadcast.BroadcastDto
import com.fm404.onair.data.remote.dto.broadcast.CreateBroadcastRequest
import retrofit2.Response
import retrofit2.http.*

interface BroadcastApi {
    @GET("broadcasts")
    suspend fun getBroadcastList(): Response<List<BroadcastDto>>

    @POST("broadcasts")
    suspend fun createBroadcast(
        @Body request: CreateBroadcastRequest
    ): Response<BroadcastDto>
}