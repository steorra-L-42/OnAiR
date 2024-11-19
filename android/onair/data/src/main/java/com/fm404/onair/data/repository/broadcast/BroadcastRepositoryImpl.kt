package com.fm404.onair.data.repository.broadcast

import com.fm404.onair.core.network.manager.TokenManager
import com.fm404.onair.data.mapper.toBroadcast
import com.fm404.onair.data.mapper.toChannelList
import com.fm404.onair.data.mapper.toCreateChannelResult
import com.fm404.onair.data.mapper.toDomain
import com.fm404.onair.data.mapper.toPlayListDto
import com.fm404.onair.data.remote.api.broadcast.BroadcastApi
import com.fm404.onair.domain.model.broadcast.Broadcast
import com.fm404.onair.data.remote.dto.broadcast.CreateChannelRequest
import com.fm404.onair.domain.model.broadcast.Channel
import com.fm404.onair.domain.model.broadcast.ChannelList
import com.fm404.onair.domain.model.broadcast.CreateChannelPlayList
import com.fm404.onair.domain.model.broadcast.CreateChannelResult
import com.fm404.onair.domain.repository.broadcast.BroadcastRepository
import javax.inject.Inject

class BroadcastRepositoryImpl @Inject constructor(
    private val api: BroadcastApi,
    private val tokenManager: TokenManager
) : BroadcastRepository {

    // 채널 목록 조회
    override suspend fun getChannelList(): Result<List<ChannelList>> = runCatching {
        val response = api.getChannelList()
        if (response.isSuccessful) {
            response.body()?.channelList?.map { it.toChannelList() }
                ?: throw Exception("Empty response body")
        } else {
            throw Exception("Failed to fetch channel list")
        }
    }

    // 방송 생성
    override suspend fun createChannel(
        ttsEngine: String,
        personality: String,
        newsTopic: String,
        thumbnail: String,
        channelName: String,
        trackList: List<CreateChannelPlayList>
    ): Result<CreateChannelResult> = runCatching {
        val request = CreateChannelRequest(
            ttsEngine = ttsEngine,
            personality = personality,
            newsTopic = newsTopic,
            thumbnail = thumbnail,
            channelName = channelName,
            trackList = trackList.map { it.toPlayListDto() }
        )

        val response = api.createChannel(request)
        if (response.isSuccessful) {
            response.body()?.toCreateChannelResult() ?: throw Exception("Empty response body")
        } else {
            throw Exception("Failed to create channel")
        }
    }

    // 테스트용
    override suspend fun getBroadcastList(): Result<List<Broadcast>> = runCatching {
        val response = api.getBroadcastList()
        if (response.isSuccessful) {
            response.body()?.map { it.toBroadcast() } ?: emptyList()
        } else {
            throw Exception(response.message())
        }
    }

    // 채널 상세 조회
    override suspend fun getChannel(channelId: String): Result<Channel> = runCatching {
        val response = api.getChannel(channelId)
        if (response.isSuccessful) {
            response.body()?.toDomain() ?: throw Exception("Empty response body")
        } else {
            throw Exception("Failed to fetch channel: ${response.message()}")
        }
    }
}