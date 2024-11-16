package com.fm404.onair.features.broadcast.presentation.list.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.fm404.onair.domain.model.broadcast.ChannelList
import com.fm404.onair.features.broadcast.presentation.list.BroadcastListViewModel
import com.fm404.onair.features.broadcast.presentation.list.screen.component.ChannelItem
import com.fm404.onair.features.broadcast.presentation.list.state.BroadcastListState

@Composable
fun BroadcastListScreen(
    onBroadcastClick: (String) -> Unit,
    onCreateClick: () -> Unit,
    viewModel: BroadcastListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        BroadcastListContent(
            state = state,
            onBroadcastClick = onBroadcastClick,
            onChannelClick = { channel ->
                onBroadcastClick(channel.channelUuid)
            },
            onNotificationClick = viewModel::onNotificationClick
        )

        FloatingActionButton(
            onClick = onCreateClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "방송 만들기"
            )
        }

        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }

        state.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun BroadcastListContent(
    state: BroadcastListState,
    onBroadcastClick: (String) -> Unit,
    onChannelClick: (ChannelList) -> Unit,
    onNotificationClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "방송 목록",
                style = MaterialTheme.typography.headlineMedium
            )

            IconButton(onClick = onNotificationClick) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "알림"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onBroadcastClick("1") }
        ) {
            Text("방송 1로 이동")
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = state.channels,
                key = { it.channelUuid }
            ) { channel ->
                ChannelItem(
                    channelList = channel,
                    onClick = { onChannelClick(channel) }
                )
            }
        }
    }
}