package com.fm404.onair.domain.repository.broadcast

import com.fm404.onair.domain.model.broadcast.Channel
import com.fm404.onair.domain.model.broadcast.CreateChannelPlayList
import com.fm404.onair.domain.model.broadcast.CreateChannelResult

interface BroadcastRepository {
    suspend fun getChannelList(): Result<List<Channel>>
    suspend fun createChannel(
        ttsEngine: String,
        personality: String,
        topic: String,
        playList: List<CreateChannelPlayList>
    ): Result<CreateChannelResult>
}