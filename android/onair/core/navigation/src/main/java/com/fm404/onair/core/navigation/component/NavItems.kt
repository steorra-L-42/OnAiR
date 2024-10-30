package com.fm404.onair.core.navigation.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Settings
import com.fm404.onair.core.navigation.model.BottomNavItem
import com.fm404.onair.core.navigation.model.NavRoute

object HomeNavItem : BottomNavItem(NavRoute.Home.route, "홈", Icons.Default.Home)
object StatisticsNavItem : BottomNavItem(NavRoute.Statistics.route, "통계", Icons.Default.Analytics)
object SettingsNavItem : BottomNavItem(NavRoute.Settings.route, "설정", Icons.Default.Settings)

val mainBottomNavItems = listOf(HomeNavItem, StatisticsNavItem, SettingsNavItem)