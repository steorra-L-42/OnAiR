package com.fm404.onair.di

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.fm404.onair.presentation.main.screen.home.HomeScreen
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeScreenProvider @Inject constructor() {
    val homeScreen: @Composable (NavHostController) -> Unit = { navController ->
        HomeScreen(navController = navController)
    }
}