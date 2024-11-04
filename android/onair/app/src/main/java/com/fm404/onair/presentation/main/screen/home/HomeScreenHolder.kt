package com.fm404.onair.presentation.main.screen.home


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import javax.inject.Inject

class HomeScreenHolder @Inject constructor() {
    val homeScreen: @Composable (NavHostController) -> Unit = { navController ->
        HomeScreen(navController = navController)
    }
}