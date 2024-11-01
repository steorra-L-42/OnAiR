package com.fm404.onair.di

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.fm404.onair.core.contract.auth.AuthScreen
import com.fm404.onair.features.auth.di.AuthScreenImpl
import com.fm404.onair.presentation.main.screen.home.HomeScreenHolder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ScreenModule {
    @Provides
    @Singleton
    fun provideHomeScreenHolder(): HomeScreenHolder = HomeScreenHolder()

//    @Provides
//    @Singleton
//    fun provideBroadcastScreen(): BroadcastScreen = BroadcastScreenImpl()
//
//    @Provides
//    @Singleton
//    fun provideMediaScreen(): MediaScreen = MediaScreenImpl()
//
//    @Provides
//    @Singleton
//    fun provideStatisticsScreen(): StatisticsScreen = StatisticsScreenImpl()
}