package com.fm404.onair.core.navigation.impl

import androidx.navigation.NavController
import com.fm404.onair.core.contract.statistics.StatisticsNavigationContract
import com.fm404.onair.core.navigation.model.NavRoute
import javax.inject.Inject

class StatisticsNavigationContractImpl @Inject constructor() : StatisticsNavigationContract {
    private var navController: NavController? = null

    override fun setNavController(navController: NavController) {
        this.navController = navController
    }

    override fun navigateToBroadcastStatistics() {
        navController?.navigate(NavRoute.StatisticsSection.Broadcast.route)
    }

    override fun navigateToStoryStatistics() {
        navController?.navigate(NavRoute.StatisticsSection.Story.route)
    }
}