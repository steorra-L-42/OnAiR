package com.fm404.onair.core.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.fm404.onair.core.contract.statistics.StatisticsScreen
import com.fm404.onair.core.navigation.model.NavRoute

fun NavGraphBuilder.statisticsNavGraph(
    navController: NavHostController,
    statisticsScreen: StatisticsScreen
) {
    navigation(
        startDestination = NavRoute.StatisticsSection.Main.route,
        route = NavRoute.MainSection.Statistics.route
    ) {
        composable(NavRoute.StatisticsSection.Main.route) {
            statisticsScreen.StatisticsRoute(navController)
        }
        composable(NavRoute.StatisticsSection.Broadcast.route) {
            statisticsScreen.BroadcastStatisticsRoute(navController)
        }
        composable(NavRoute.StatisticsSection.Story.route) {
            statisticsScreen.StoryStatisticsRoute(navController)
        }
    }
}