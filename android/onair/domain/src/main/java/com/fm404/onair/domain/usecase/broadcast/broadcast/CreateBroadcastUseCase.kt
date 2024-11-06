package com.fm404.onair.domain.usecase.broadcast.broadcast

import com.fm404.onair.domain.model.broadcast.Broadcast
import com.fm404.onair.domain.repository.broadcast.BroadcastRepository
import javax.inject.Inject

class CreateBroadcastUseCase @Inject constructor(
    private val repository: BroadcastRepository
) {
    suspend operator fun invoke(title: String, description: String): Result<Broadcast> {
        if (title.isBlank()) {
            return Result.failure(IllegalArgumentException("방송 제목을 입력해주세요."))
        }

        return repository.createBroadcast(title, description)
    }
}