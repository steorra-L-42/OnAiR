package com.fm404.onair.di

import com.fm404.onair.domain.repository.story.StoryRepository
import com.fm404.onair.data.repository.story.StoryRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StoryModule {
}
