package com.fm404.onair.domain.usecase.broadcast.broadcast

import com.fm404.onair.domain.model.broadcast.Channel
import com.fm404.onair.domain.repository.broadcast.BroadcastRepository
import javax.inject.Inject

class GetChannelListUseCase @Inject constructor(
    private val repository: BroadcastRepository
) {
    suspend operator fun invoke(): Result<List<Channel>> {
        return repository.getChannelList()
    }
}