package com.fm404.onair.data.repository.story

import com.fm404.onair.data.mapper.toDomain
import com.fm404.onair.data.mapper.toDto
import com.fm404.onair.data.remote.api.story.StoryApi
import com.fm404.onair.domain.model.story.CreateStoryRequest
import com.fm404.onair.domain.model.story.Story
import com.fm404.onair.domain.repository.story.StoryRepository
import javax.inject.Inject

class StoryRepositoryImpl @Inject constructor(
    private val storyApi: StoryApi
) : StoryRepository {
    override suspend fun createStory(
        channelUuid: String,
        request: CreateStoryRequest
    ): Result<Story> = runCatching {
        val response = storyApi.createStory(channelUuid, request.toDto())
        if (response.isSuccessful) {
            response.body()?.toDomain() ?: throw Exception("Empty response body")
        } else {
            throw Exception("Failed to create story")
        }
    }
}