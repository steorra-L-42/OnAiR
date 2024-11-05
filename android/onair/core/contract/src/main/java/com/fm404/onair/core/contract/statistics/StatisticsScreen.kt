package com.fm404.onair.core.contract.statistics

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

interface StatisticsScreen {
    @Composable
    fun StatisticsRoute(navController: NavHostController)

    @Composable
    fun BroadcastStatisticsRoute(navController: NavHostController)

    @Composable
    fun StoryStatisticsRoute(navController: NavHostController)
}