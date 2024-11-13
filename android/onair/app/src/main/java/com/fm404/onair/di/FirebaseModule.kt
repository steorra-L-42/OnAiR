package com.fm404.onair.di

import com.fm404.onair.core.contract.auth.FCMServiceContract
import com.fm404.onair.core.firebase.FCMService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    @Provides
    @Singleton
    fun provideFCMService(): FCMService {
        return FCMService()
    }

    @Provides
    @Singleton
    fun provideFCMServiceContract(fcmService: FCMService): FCMServiceContract {
        return fcmService
    }
}