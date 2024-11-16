package com.fm404.onair.domain.repository.broadcast

import com.fm404.onair.domain.model.broadcast.Broadcast
import com.fm404.onair.domain.model.broadcast.Channel
import com.fm404.onair.domain.model.broadcast.ChannelList
import com.fm404.onair.domain.model.broadcast.CreateChannelPlayList
import com.fm404.onair.domain.model.broadcast.CreateChannelResult

interface BroadcastRepository {
    suspend fun getChannelList(): Result<List<ChannelList>>
    suspend fun createChannel(
        ttsEngine: String,
        personality: String,
        newsTopic: String,
        thumbnail: String,
        channelName: String,
        trackList: List<CreateChannelPlayList>
    ): Result<CreateChannelResult>
    suspend fun getChannel(channelId: String): Result<Channel>

    suspend fun getBroadcastList(): Result<List<Broadcast>>
}