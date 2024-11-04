package com.fm404.onair.features.auth.di

import com.fm404.onair.core.contract.auth.AuthScreen
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthScreenModule {
    @Provides
    @Singleton
    fun provideAuthScreen(): AuthScreen = AuthScreenImpl()
}