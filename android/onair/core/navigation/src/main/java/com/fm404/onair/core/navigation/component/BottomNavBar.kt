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
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
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