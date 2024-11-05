package com.fm404.onair.core.contract.broadcast

import androidx.navigation.NavController

interface BroadcastNavigationContract {
    fun setNavController(navController: NavController)
    fun navigateToBroadcastHome()
    fun navigateToBroadcastLive()
    fun navigateToBroadcastSettings()
}