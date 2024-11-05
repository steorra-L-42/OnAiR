package com.fm404.onair.core.contract.broadcast

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

interface BroadcastScreen {
    fun NavGraphBuilder.addBroadcastHome(navController: NavHostController)
    fun NavGraphBuilder.addBroadcastLive(navController: NavHostController)
    fun NavGraphBuilder.addBroadcastSettings(navController: NavHostController)
}