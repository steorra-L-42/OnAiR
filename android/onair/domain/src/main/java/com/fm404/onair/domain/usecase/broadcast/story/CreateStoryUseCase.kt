package com.fm404.onair.domain.usecase.broadcast.story

import com.fm404.onair.domain.model.story.CreateStoryRequest
import com.fm404.onair.domain.model.story.Story
import com.fm404.onair.domain.repository.story.StoryRepository
import javax.inject.Inject

class CreateStoryUseCase @Inject constructor(
    private val repository: StoryRepository
) {
    suspend operator fun invoke(channelUuid: String, request: CreateStoryRequest): Result<Story> {
        return repository.createStory(channelUuid, request)
    }
}