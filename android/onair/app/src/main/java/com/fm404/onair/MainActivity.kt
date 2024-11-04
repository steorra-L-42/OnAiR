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
import com.fm404.onair.core.contract.auth.AuthScreen
import com.fm404.onair.core.designsystem.theme.OnAirTheme
import com.fm404.onair.core.navigation.component.BottomNavBar
import com.fm404.onair.core.navigation.graph.MainNavGraph
import com.fm404.onair.core.navigation.model.NavRoute
import com.fm404.onair.presentation.main.screen.home.HomeScreenHolder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var homeScreenHolder: HomeScreenHolder

    @Inject
    lateinit var authScreen: AuthScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            OnAirTheme {
                MainScreen(
                    homeScreen = homeScreenHolder.homeScreen,
                    authScreen = authScreen
                )
            }
        }
    }
}

@Composable
private fun MainScreen(
    modifier: Modifier = Modifier,
    homeScreen: @Composable (NavHostController) -> Unit,
    authScreen: AuthScreen
) {
    val navController = rememberNavController()

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
                homeScreen = homeScreen,
                authScreen = authScreen
            )
`        }
`    }
}