package com.fm404.onair.features.broadcast.presentation.list.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.fm404.onair.features.broadcast.presentation.list.BroadcastListViewModel
import com.fm404.onair.features.broadcast.presentation.list.state.BroadcastListState

@Composable
fun BroadcastListScreen(
    onBroadcastClick: (String) -> Unit,
    viewModel: BroadcastListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    BroadcastListContent(
        state = state,
        onBroadcastClick = onBroadcastClick
    )
}

@Composable
private fun BroadcastListContent(
    state: BroadcastListState,
    onBroadcastClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "방송 목록",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 임시 버튼
        Button(
            onClick = { onBroadcastClick("1") }
        ) {
            Text("방송 1로 이동")
        }
    }
}