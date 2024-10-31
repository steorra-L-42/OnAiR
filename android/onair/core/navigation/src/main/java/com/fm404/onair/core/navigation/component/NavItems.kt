package com.fm404.onair.core.navigation.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.ui.graphics.vector.ImageVector
import com.fm404.onair.core.navigation.model.NavRoute

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

val mainBottomNavItems = listOf(
    BottomNavItem(
        route = NavRoute.MainSection.Home.route,
        icon = Icons.Default.Home,
        label = "Home"
    ),
    BottomNavItem(
        route = NavRoute.MainSection.Statistics.route,
        icon = Icons.Default.ShowChart,
        label = "Statistics"
    ),
    BottomNavItem(
        route = NavRoute.MainSection.Settings.route,
        icon = Icons.Default.Settings,
        label = "Settings"
    )
)