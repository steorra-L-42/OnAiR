package com.fm404.onair.domain.usecase.broadcast.broadcast

import com.fm404.onair.domain.repository.broadcast.BroadcastRepository
import javax.inject.Inject

class GetChannelUseCase @Inject constructor(
    private val repository: BroadcastRepository
) {
    suspend operator fun invoke(channelId: String) = repository.getChannel(channelId)
}