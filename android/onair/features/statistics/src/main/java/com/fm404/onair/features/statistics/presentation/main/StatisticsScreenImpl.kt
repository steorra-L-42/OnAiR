package com.fm404.onair.features.statistics.presentation.main

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.fm404.onair.core.contract.statistics.StatisticsNavigationContract
import com.fm404.onair.core.contract.statistics.StatisticsScreen
import com.fm404.onair.features.statistics.presentation.broadcast.screen.BroadcastStatisticsScreen
import com.fm404.onair.features.statistics.presentation.main.screen.StatisticsMainScreen
import com.fm404.onair.features.statistics.presentation.story.screen.StoryStatisticsScreen
import javax.inject.Inject

class StatisticsScreenImpl @Inject constructor(
    private val statisticsNavigationContract: StatisticsNavigationContract
) : StatisticsScreen {

    @Composable
    override fun StatisticsRoute(navController: NavHostController) {
        val viewModel: StatisticsViewModel = hiltViewModel()
        StatisticsMainScreen(
            navController = navController,
            viewModel = viewModel,
            statisticsNavigationContract = statisticsNavigationContract
        )
    }

    @Composable
    override fun BroadcastStatisticsRoute(navController: NavHostController) {
        BroadcastStatisticsScreen(
            navController = navController
        )
    }

    @Composable
    override fun StoryStatisticsRoute(navController: NavHostController) {
        StoryStatisticsScreen(
            navController = navController
        )
    }
}