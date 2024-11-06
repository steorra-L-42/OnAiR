package com.fm404.onair.core.contract.broadcast

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

interface BroadcastScreen {
    @Composable
    fun BroadcastListRoute(navController: NavHostController)

    @Composable
    fun BroadcastDetailRoute(navController: NavHostController)

    @Composable
    fun StoryRoute(navController: NavHostController)

    @Composable
    fun NotificationRoute(navController: NavHostController)

    @Composable
    fun BroadcastCreateRoute(navController: NavHostController)
}