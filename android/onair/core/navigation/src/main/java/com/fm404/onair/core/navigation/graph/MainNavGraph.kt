package com.fm404.onair.core.navigation.graph


import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.fm404.onair.core.contract.auth.AuthScreen
import com.fm404.onair.core.contract.broadcast.BroadcastScreen
import com.fm404.onair.core.contract.statistics.StatisticsScreen
import com.fm404.onair.core.designsystem.component.audiovisualizer.AudioVisualizerScreen
import com.fm404.onair.core.navigation.model.NavRoute

@Composable
fun MainNavGraph(
    navController: NavHostController,
    startDestination: String = NavRoute.MainSection.Home.route,
    homeScreen: @Composable (NavHostController) -> Unit,
    authScreen: AuthScreen,
    broadcastScreen: BroadcastScreen,
    statisticsScreen: StatisticsScreen
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        authNavGraph(
            navController = navController,
            authScreen = authScreen
        )

        broadcastNavGraph(
            navController = navController,
            broadcastScreen = broadcastScreen
        )

        statisticsNavGraph(
            navController = navController,
            statisticsScreen = statisticsScreen
        )

        // 메인 섹션 화면들
        composable(NavRoute.MainSection.Home.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            homeScreen(navController)
        }

        composable(NavRoute.MainSection.Statistics.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            // StatisticsScreen()
        }

        composable(NavRoute.MainSection.Settings.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            // SettingsScreen()
        }

        // 홈 섹션의 하위 화면들
        composable(NavRoute.HomeSection.AudioVisualizer.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            AudioVisualizerScreen(
                amplitudes = FloatArray(10) { 5f }
            )
        }
    }
}
