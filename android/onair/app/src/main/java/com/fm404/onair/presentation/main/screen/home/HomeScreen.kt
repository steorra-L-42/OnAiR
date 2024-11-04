package com.fm404.onair.presentation.main.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.fm404.onair.core.navigation.model.NavRoute
import com.fm404.onair.presentation.main.screen.home.state.HomeState
import com.fm404.onair.presentation.main.screen.home.state.HomeEvent

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    HomeContent(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateToAudioVisualizer = {
            navController.navigate(NavRoute.HomeSection.AudioVisualizer.route)
        },
        onNavigateToLogin = {
            navController.navigate(NavRoute.AuthSection.Login.route)
        }
    )
}

@Composable
private fun HomeContent(
    state: HomeState,
    onEvent: (HomeEvent) -> Unit,
    onNavigateToAudioVisualizer: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Welcome to OnAir",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNavigateToAudioVisualizer
        ) {
            Text("Go to Audio Visualizer")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNavigateToLogin
        ) {
            Text("Login")
        }
    }
}