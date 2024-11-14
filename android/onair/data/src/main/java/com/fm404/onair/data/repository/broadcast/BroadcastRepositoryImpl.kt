package com.fm404.onair.data.repository.broadcast

import com.fm404.onair.data.mapper.toChannel
import com.fm404.onair.data.mapper.toCreateChannelResult
import com.fm404.onair.data.mapper.toPlayListDto
import com.fm404.onair.data.remote.api.broadcast.BroadcastApi
import com.fm404.onair.data.remote.dto.broadcast.CreateChannelRequest
import com.fm404.onair.domain.model.broadcast.Channel
import com.fm404.onair.domain.model.broadcast.CreateChannelPlayList
import com.fm404.onair.domain.model.broadcast.CreateChannelResult
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

    override suspend fun createChannel(
        ttsEngine: String,
        personality: String,
        topic: String,
        playList: List<CreateChannelPlayList>
    ): Result<CreateChannelResult> = runCatching {
        val request = CreateChannelRequest(
            ttsEngine = ttsEngine,
            personality = personality,
            topic = topic,
            playList = playList.map { it.toPlayListDto() }
        )

        val response = api.createChannel(request)
        if (response.isSuccessful) {
            response.body()?.toCreateChannelResult() ?: throw Exception("Empty response body")
        } else {
            throw Exception("Failed to create channel")
        }
    }
}