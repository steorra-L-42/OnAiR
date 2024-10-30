package com.fm404.onair

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.fm404.onair.core.designsystem.theme.OnAirTheme
import com.fm404.onair.core.navigation.component.BottomNavBar
import com.fm404.onair.core.navigation.graph.MainNavGraph
import com.fm404.onair.core.navigation.model.NavRoute
import com.fm404.onair.di.HomeScreenProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var homeScreenProvider: HomeScreenProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            OnAirTheme {
                MainScreen(homeScreen = homeScreenProvider.homeScreen)
            }
        }
    }
}

@Composable
private fun MainScreen(
    modifier: Modifier = Modifier,
    homeScreen: @Composable (NavHostController) -> Unit
) {
    val navController = rememberNavController()
    val startDestination = remember { NavRoute.Home.route }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            BottomNavBar(
                navController = navController
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            MainNavGraph(
                navController = navController,
                startDestination = startDestination,
                homeScreen = homeScreen
            )
        }
    }
}