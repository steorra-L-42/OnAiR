package com.fm404.onair.di


import android.content.Context
import com.fm404.onair.OnAirApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // 앱 전반의 공통 의존성들...
    // 예: SharedPreferences, Database 등

    @Provides
    @Singleton
    fun provideContext(
        application: OnAirApplication
    ): Context = application.applicationContext
}