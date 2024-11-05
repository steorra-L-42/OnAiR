package com.fm404.onair.data.remote.api.broadcast

import com.fm404.onair.data.remote.dto.broadcast.BroadcastDto
import retrofit2.Response
import retrofit2.http.*

interface BroadcastApi {
    @GET("broadcasts")
    suspend fun getBroadcastList(): Response<List<BroadcastDto>>
}