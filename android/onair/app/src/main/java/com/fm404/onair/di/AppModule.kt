package com.fm404.onair.di


import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // 앱 전반의 공통 의존성들...
    // 예: SharedPreferences, Database 등
}