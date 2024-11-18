package com.fm404.onair.data.di

import com.fm404.onair.core.network.annotation.PublicApi
import com.fm404.onair.data.remote.api.auth.UserApi
import com.fm404.onair.data.remote.api.broadcast.BroadcastApi
import com.fm404.onair.data.remote.api.story.StoryApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideBroadcastApi(retrofit: Retrofit): BroadcastApi {
        return retrofit.create(BroadcastApi::class.java)
    }

    @Provides
    @Singleton
    fun provideStoryApi(retrofit: Retrofit): StoryApi {
        return retrofit.create(StoryApi::class.java)
    }

}