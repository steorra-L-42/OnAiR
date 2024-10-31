package com.fm404.onair.core.navigation.model

sealed class NavDestination {
    data object Home : NavDestination()
    data object Statistics : NavDestination()
    data object Settings : NavDestination()
    data object AudioVisualizer : NavDestination()

    companion object {
        fun fromRoute(route: Any): NavDestination {
            return when (route) {
                is NavRoute.MainSection -> when (route) {
                    NavRoute.MainSection.Home -> Home
                    NavRoute.MainSection.Statistics -> Statistics
                    NavRoute.MainSection.Settings -> Settings
                }
                is NavRoute.HomeSection -> when (route) {
                    NavRoute.HomeSection.AudioVisualizer -> AudioVisualizer
                }
                else -> Home // 기본값
            }
        }
    }
}