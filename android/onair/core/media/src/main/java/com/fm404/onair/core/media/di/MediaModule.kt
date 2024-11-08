package com.fm404.onair.core.media.di

import android.content.Context
import com.fm404.onair.core.contract.media.MediaPlayerContract
import com.fm404.onair.core.media.impl.MediaPlayerContractImpl
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object MediaModule {
    @Provides
    @Singleton
    fun provideMediaPlayerContract(
        @ApplicationContext context: Context
    ): MediaPlayerContract {
        return MediaPlayerContractImpl(context)
    }
}