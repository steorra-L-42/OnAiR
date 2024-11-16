package com.fm404.onair.domain.repository.story

import com.fm404.onair.domain.model.story.CreateStoryRequest
import com.fm404.onair.domain.model.story.Story

interface StoryRepository {
    suspend fun createStory(channelUuid: String, request: CreateStoryRequest): Result<Story>
}