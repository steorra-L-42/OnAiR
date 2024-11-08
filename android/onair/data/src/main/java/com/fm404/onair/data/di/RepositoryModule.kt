package com.fm404.onair.data.di

import com.fm404.onair.data.repository.auth.UserRepositoryImpl
import com.fm404.onair.data.repository.broadcast.BroadcastRepositoryImpl
import com.fm404.onair.data.repository.media.MediaStreamingRepositoryImpl
import com.fm404.onair.domain.repository.auth.UserRepository
import com.fm404.onair.domain.repository.broadcast.BroadcastRepository
import com.fm404.onair.domain.repository.media.MediaStreamingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    abstract fun bindBroadcastRepository(
        repositoryImpl: BroadcastRepositoryImpl
    ): BroadcastRepository

    @Binds
    @Singleton
    abstract fun bindMediaStreamingRepository(
        repository: MediaStreamingRepositoryImpl
    ): MediaStreamingRepository
}