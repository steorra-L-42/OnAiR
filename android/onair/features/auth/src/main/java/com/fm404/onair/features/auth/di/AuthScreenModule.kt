package com.fm404.onair.features.auth.di

import com.fm404.onair.core.contract.auth.AuthScreen
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.fm404.onair.features.auth.di.AuthScreenImpl
import dagger.Binds

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthScreenModule {
    @Binds
    @Singleton
    abstract fun bindAuthScreen(
        authScreenImpl: AuthScreenImpl
    ): AuthScreen
}