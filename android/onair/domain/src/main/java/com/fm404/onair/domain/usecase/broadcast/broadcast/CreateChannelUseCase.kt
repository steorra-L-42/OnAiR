package com.fm404.onair.domain.usecase.broadcast.broadcast

import com.fm404.onair.domain.model.broadcast.CreateChannelPlayList
import com.fm404.onair.domain.model.broadcast.CreateChannelResult
import com.fm404.onair.domain.repository.broadcast.BroadcastRepository
import javax.inject.Inject

class CreateChannelUseCase @Inject constructor(
    private val repository: BroadcastRepository
) {
    suspend operator fun invoke(
        ttsEngine: String,
        personality: String,
        topic: String,
        playList: List<CreateChannelPlayList>
    ): Result<CreateChannelResult> {
        return repository.createChannel(ttsEngine, personality, topic, playList)
    }
}