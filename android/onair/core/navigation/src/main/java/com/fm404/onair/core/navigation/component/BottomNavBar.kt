package com.fm404.onair.core.navigation.component

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.fm404.onair.core.navigation.model.NavRoute

@Composable
fun BottomNavBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isInStatisticsSection = currentRoute?.startsWith(NavRoute.MainSection.Statistics.route) ?: false

    NavigationBar(modifier = modifier) {
        mainBottomNavItems.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(text = item.label) },
                selected = when {
                    // 통계 섹션에 있을 때는 통계 탭이 선택된 상태로 표시
                    isInStatisticsSection && item.route == NavRoute.MainSection.Statistics.route -> true
                    else -> currentRoute == item.route
                },
                onClick = {
                    if (currentRoute != item.route || (isInStatisticsSection && item.route == NavRoute.MainSection.Statistics.route)) {
                        val targetRoute = if (item.route == NavRoute.MainSection.Statistics.route) {
                            NavRoute.StatisticsSection.Main.route
                        } else {
                            item.route
                        }

                        navController.navigate(targetRoute) {
                            popUpTo(navController.graph.startDestinationId) {
                                if (item.route == NavRoute.MainSection.Home.route) {
                                    inclusive = false
                                    saveState = false
                                } else {
                                    saveState = true
                                }
                            }
                            launchSingleTop = true
                            restoreState = item.route != NavRoute.MainSection.Home.route
                        }
                    }
                }
            )
        }
    }
}