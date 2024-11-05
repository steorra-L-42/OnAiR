package com.fm404.onair.di

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.fm404.onair.core.contract.auth.AuthScreen
import com.fm404.onair.core.contract.broadcast.BroadcastNavigationContract
import com.fm404.onair.core.contract.broadcast.BroadcastScreen
import com.fm404.onair.core.contract.statistics.StatisticsNavigationContract
import com.fm404.onair.core.contract.statistics.StatisticsScreen
import com.fm404.onair.core.navigation.impl.StatisticsNavigationContractImpl
import com.fm404.onair.features.auth.di.AuthScreenImpl
import com.fm404.onair.features.broadcast.presentation.list.BroadcastScreenImpl
import com.fm404.onair.features.statistics.presentation.main.StatisticsScreenImpl
import com.fm404.onair.features.statistics.presentation.main.StatisticsViewModel
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

    @Provides
    @Singleton
    fun provideStatisticsNavigationContract(): StatisticsNavigationContract = StatisticsNavigationContractImpl()

    @Provides
    @Singleton
    fun provideStatisticsScreen(
        statisticsNavigationContract: StatisticsNavigationContract
    ): StatisticsScreen = StatisticsScreenImpl(statisticsNavigationContract)

    @Provides
    @Singleton
    fun provideBroadcastScreen(
        broadcastNavigationContract: BroadcastNavigationContract
    ): BroadcastScreen = BroadcastScreenImpl(broadcastNavigationContract)

//    @Provides
//    @Singleton
//    fun provideMediaScreen(): MediaScreen = MediaScreenImpl()
//
//    @Provides
//    @Singleton
//    fun provideStatisticsScreen(): StatisticsScreen = StatisticsScreenImpl()
}