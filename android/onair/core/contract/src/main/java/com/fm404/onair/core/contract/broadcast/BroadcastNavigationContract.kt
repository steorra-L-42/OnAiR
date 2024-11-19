package com.fm404.onair.core.contract.broadcast

import androidx.navigation.NavController

interface BroadcastNavigationContract {
    fun setNavController(navController: NavController)
    fun navigateToBroadcastList()
    fun navigateToBroadcastDetail(broadcastId: String)
    fun navigateToStory(broadcastId: String)
    fun navigateToNotification()
    fun navigateToBroadcastCreate()
}