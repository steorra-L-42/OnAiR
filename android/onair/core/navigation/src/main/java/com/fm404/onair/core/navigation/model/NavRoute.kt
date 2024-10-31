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
}