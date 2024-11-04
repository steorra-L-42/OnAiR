package com.fm404.onair.features.auth.di

import com.fm404.onair.domain.repository.auth.UserRepository
import com.fm404.onair.domain.usecase.auth.CheckAdminRoleUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object AdminModule {
    @Provides
    @ViewModelScoped
    fun provideCheckAdminRoleUseCase(
        userRepository: UserRepository
    ): CheckAdminRoleUseCase {
        return CheckAdminRoleUseCase(userRepository)
    }
}