package com.fm404.onair.data.remote.api.story

import com.fm404.onair.data.remote.dto.story.CreateStoryRequestDto
import com.fm404.onair.data.remote.dto.story.StoryResponseDto
import retrofit2.http.*
import retrofit2.Response

interface StoryApi {
    @POST("api/v1/story/{channel_uuid}")
    suspend fun createStory(
        @Path("channel_uuid") channelUuid: String,
        @Body request: CreateStoryRequestDto
    ): Response<StoryResponseDto>
}
