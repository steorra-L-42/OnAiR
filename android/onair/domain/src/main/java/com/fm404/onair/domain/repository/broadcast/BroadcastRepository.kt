package com.fm404.onair.domain.repository.broadcast

import com.fm404.onair.domain.model.broadcast.Channel

interface BroadcastRepository {
    suspend fun getChannelList(): Result<List<Channel>>
}