package com.fm404.onair.core.navigation.graph


import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.fm404.onair.core.contract.auth.AuthScreen
import com.fm404.onair.core.designsystem.component.audiovisualizer.AudioVisualizerScreen
import com.fm404.onair.core.navigation.model.NavRoute

@Composable
fun MainNavGraph(
    navController: NavHostController,
    startDestination: String = NavRoute.MainSection.Home.route,
    homeScreen: @Composable (NavHostController) -> Unit,
    authScreen: AuthScreen
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        authNavGraph(
            navController = navController,
            authScreen = authScreen
        )

        // 메인 섹션 화면들
        composable(NavRoute.MainSection.Home.route) {
            homeScreen(navController)
        }

        composable(NavRoute.MainSection.Statistics.route) {
            // StatisticsScreen()
        }

        composable(NavRoute.MainSection.Settings.route) {
            // SettingsScreen()
        }

        // 홈 섹션의 하위 화면들
        composable(NavRoute.HomeSection.AudioVisualizer.route) {
            AudioVisualizerScreen(
                amplitudes = FloatArray(10) { 5f }
            )
        }
    }
}