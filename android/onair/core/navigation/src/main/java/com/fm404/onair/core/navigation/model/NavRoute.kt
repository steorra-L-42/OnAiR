package com.fm404.onair.core.navigation.model

sealed class NavRoute(val route: String) {
    data object Home : NavRoute("home")
    data object Statistics : NavRoute("statistics")
    data object Settings : NavRoute("settings")
    data object AudioVisualizer : NavRoute("audio_visualizer")

    companion object {
        fun fromRoute(route: String?): NavRoute {
            return when(route) {
                "home" -> Home
                "statistics" -> Statistics
                "settings" -> Settings
                "audio_visualizer" -> AudioVisualizer
                else -> Home
            }
        }
    }
}