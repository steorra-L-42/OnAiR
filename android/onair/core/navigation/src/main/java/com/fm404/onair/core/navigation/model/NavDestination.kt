package com.fm404.onair.core.navigation.model

sealed class NavDestination {
    data object Home : NavDestination()
    data object Statistics : NavDestination()
    data object Settings : NavDestination()
    data object AudioVisualizer : NavDestination()

    companion object {
        fun fromRoute(route: NavRoute): NavDestination {
            return when(route) {
                is NavRoute.Home -> Home
                is NavRoute.Statistics -> Statistics
                is NavRoute.Settings -> Settings
                is NavRoute.AudioVisualizer -> AudioVisualizer
            }
        }
    }
}