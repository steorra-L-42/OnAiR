package com.fm404.onair.features.broadcast.presentation.detail.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BroadcastDetailScreen(
    broadcastId: String,
    onStoryClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "방송 상세 화면: $broadcastId",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onStoryClick(broadcastId) }
        ) {
            Text("사연 보기")
        }
    }
}