package com.fm404.onair.features.statistics.presentation.main.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.fm404.onair.core.contract.statistics.StatisticsNavigationContract
import com.fm404.onair.features.statistics.presentation.main.StatisticsViewModel
import com.fm404.onair.features.statistics.presentation.main.state.StatisticsEvent

@Composable
fun StatisticsMainScreen(
    navController: NavHostController,
    viewModel: StatisticsViewModel,
    statisticsNavigationContract: StatisticsNavigationContract
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "통계",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.onEvent(StatisticsEvent.NavigateToBroadcastStatistics)
                statisticsNavigationContract.navigateToBroadcastStatistics()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("방송 통계")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.onEvent(StatisticsEvent.NavigateToStoryStatistics)
                statisticsNavigationContract.navigateToStoryStatistics()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("사연 통계")
        }

        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally)
            )
        }
    }
}