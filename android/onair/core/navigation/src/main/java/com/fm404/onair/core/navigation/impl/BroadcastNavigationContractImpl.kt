package com.fm404.onair.core.navigation.impl

import androidx.navigation.NavController
import com.fm404.onair.core.contract.broadcast.BroadcastNavigationContract
import com.fm404.onair.core.navigation.model.NavRoute
import javax.inject.Inject

class BroadcastNavigationContractImpl @Inject constructor() : BroadcastNavigationContract {
    private var navController: NavController? = null

    override fun setNavController(navController: NavController) {
        this.navController = navController
    }

    override fun navigateToBroadcastList() {
        navController?.navigate(NavRoute.BroadcastSection.List.route)
    }

    override fun navigateToBroadcastDetail(broadcastId: String) {
        navController?.navigate(NavRoute.BroadcastSection.Detail.route.replace("{broadcastId}", broadcastId))
    }

    override fun navigateToStory(broadcastId: String) {
        navController?.navigate(NavRoute.BroadcastSection.Story.route.replace("{broadcastId}", broadcastId))
    }
}