package com.fm404.onair.core.contract.statistics

import androidx.navigation.NavController

interface StatisticsNavigationContract {
    fun setNavController(navController: NavController)
    fun navigateToBroadcastStatistics()
    fun navigateToStoryStatistics()
}