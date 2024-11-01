package com.fm404.onair.features.auth.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(ViewModelComponent::class)
object AuthViewModelModule {
    // ViewModel에 필요한 다른 의존성이 있다면 여기에 추가
}