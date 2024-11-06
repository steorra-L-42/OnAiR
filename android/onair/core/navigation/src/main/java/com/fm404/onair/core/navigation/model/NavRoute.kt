package com.fm404.onair.core.navigation.model

sealed class NavRoute(val route: String) {

    // 메인 하위 라우트
    sealed class MainSection(val route: String) {
        object Home : MainSection("home")
        object Statistics : MainSection("statistics")
        object Settings : MainSection("settings")
    }

    // 홈 하위 라우트
    sealed class HomeSection(val route: String) {
        object AudioVisualizer : HomeSection("audio_visualizer")
    }

    sealed class AuthSection(route: String) : NavRoute(route) {
        object Login : AuthSection("login")
        object Register : AuthSection("register")
        object Admin : AuthSection("admin")
    }

    sealed class StatisticsSection(val route: String) {
        object Main : StatisticsSection("statistics_main")
        object Broadcast : StatisticsSection("statistics_broadcast")
        object Story : StatisticsSection("statistics_story")
    }

    sealed class BroadcastSection(route: String) : NavRoute(route) {
        object List : BroadcastSection("broadcast_list")
        object Detail : BroadcastSection("broadcast_detail/{broadcastId}")
        object Story : BroadcastSection("broadcast_story/{broadcastId}")
        object Notification : BroadcastSection("broadcast_notification")
        object Create : BroadcastSection("broadcast_create")
    }
}