package com.fm404.onair.data.repository.broadcast

import com.fm404.onair.data.mapper.toChannel
import com.fm404.onair.data.remote.api.broadcast.BroadcastApi
import com.fm404.onair.domain.model.broadcast.Channel
import com.fm404.onair.domain.repository.broadcast.BroadcastRepository
import javax.inject.Inject

class BroadcastRepositoryImpl @Inject constructor(
    private val api: BroadcastApi
) : BroadcastRepository {
    override suspend fun getChannelList(): Result<List<Channel>> = runCatching {
        val response = api.getChannelList()
        if (response.isSuccessful) {
            response.body()?.channel_list?.map { it.toChannel() }
                ?: throw Exception("Empty response body")
        } else {
            throw Exception("Failed to fetch channel list")
        }
    }
}