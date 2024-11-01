package com.fm404.onair.core.contract.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface ContractModule {
    // 다른 Contract 관련 바인딩이 있다면 여기에 추가
}