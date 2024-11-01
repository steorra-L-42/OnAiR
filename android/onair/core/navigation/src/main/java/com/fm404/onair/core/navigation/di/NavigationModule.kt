package com.fm404.onair.core.navigation.di

import com.fm404.onair.core.contract.auth.AuthNavigationContract
import com.fm404.onair.core.navigation.impl.AuthNavigationContractImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NavigationModule {
    @Binds
    @Singleton
    abstract fun bindAuthNavigationContract(
        impl: AuthNavigationContractImpl
    ): AuthNavigationContract
}