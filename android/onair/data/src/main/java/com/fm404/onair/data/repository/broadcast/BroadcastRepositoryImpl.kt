package com.fm404.onair.data.repository.broadcast

import com.fm404.onair.data.mapper.toBroadcast
import com.fm404.onair.data.remote.api.broadcast.BroadcastApi
import com.fm404.onair.domain.model.broadcast.Broadcast
import com.fm404.onair.domain.repository.broadcast.BroadcastRepository
import javax.inject.Inject

class BroadcastRepositoryImpl @Inject constructor(
    private val api: BroadcastApi
) : BroadcastRepository {
    override suspend fun getBroadcastList(): Result<List<Broadcast>> = runCatching {
        val response = api.getBroadcastList()
        if (response.isSuccessful) {
            response.body()?.map { it.toBroadcast() } ?: emptyList()
        } else {
            throw Exception(response.message())
        }
    }
}