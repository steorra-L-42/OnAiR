package com.fm404.onair.features.broadcast.di

import com.fm404.onair.features.broadcast.impl.CustomHttpDataSourceFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BroadcastModule {

    @Provides
    @Singleton
    fun provideHeaderStateFlow(): MutableStateFlow<Map<String, String>> = MutableStateFlow(emptyMap())

    @Provides
    @Singleton
    fun provideCustomHttpDataSourceFactory(headerStateFlow: MutableStateFlow<Map<String, String>>): CustomHttpDataSourceFactory {
        return CustomHttpDataSourceFactory(headerStateFlow)
    }
}
