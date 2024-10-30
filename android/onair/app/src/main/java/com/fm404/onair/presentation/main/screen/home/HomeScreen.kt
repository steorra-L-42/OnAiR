package com.fm404.onair.presentation.main.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.fm404.onair.presentation.main.screen.home.state.HomeState
import com.fm404.onair.presentation.main.screen.home.state.HomeEvent

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    HomeContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun HomeContent(
    state: HomeState,
    onEvent: (HomeEvent) -> Unit
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

        // 최근 방송 목록, 통계 요약 등 홈 화면에 필요한 컨텐츠들...
    }
}