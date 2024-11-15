package com.fm404.onair.domain.usecase.broadcast.broadcast

import com.fm404.onair.domain.model.broadcast.Broadcast
import com.fm404.onair.domain.repository.broadcast.BroadcastRepository
import javax.inject.Inject

class GetBroadcastListUseCase @Inject constructor(
    private val repository: BroadcastRepository
) {
    suspend operator fun invoke(): Result<List<Broadcast>> {
        return repository.getBroadcastList()
    }
}