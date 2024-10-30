package com.fm404.onair.core.navigation.graph


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fm404.onair.core.designsystem.component.audiovisualizer.AudioVisualizerScreen
import com.fm404.onair.core.navigation.model.NavRoute

@Composable
fun MainNavGraph(
    navController: NavHostController,
    startDestination: String = NavRoute.Home.route,
    homeScreen: @Composable (NavHostController) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavRoute.Home.route) {
            homeScreen(navController)
        }
        composable(NavRoute.Statistics.route) {
            // StatisticsScreen()
        }
        composable(NavRoute.Settings.route) {
            // SettingsScreen()
        }
        composable(NavRoute.AudioVisualizer.route) {
            AudioVisualizerScreen(
                amplitudes = FloatArray(10) { 5f }
            )
        }
    }
}