package com.fm404.onair.core.navigation.model

sealed class NavRoute(val route: String) {

    // 메인 하위 라우트
    sealed class MainSection(val route: String) {
        data object Home : MainSection("home")
        data object Statistics : MainSection("statistics")
        data object Settings : MainSection("settings")
    }

    // 홈 하위 라우트
    sealed class HomeSection(val route: String) {
        data object AudioVisualizer : HomeSection("audio_visualizer")
    }

    sealed class AuthSection(route: String) : NavRoute(route) {
        data object Login : AuthSection("login")
        data object Register : AuthSection("register")
        data object Admin : AuthSection("admin")
    }

    sealed class BroadcastSection(route: String) : NavRoute(route) {
        data object BroadcastPlayer : BroadcastSection("broadcast_player")
    }

    sealed class StatisticsSection(val route: String) {
        object Main : StatisticsSection("statistics_main")
        object Broadcast : StatisticsSection("statistics_broadcast")
        object Story : StatisticsSection("statistics_story")
    }
}