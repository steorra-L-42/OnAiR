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
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
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
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = state.isLoading)

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = viewModel::retryLoad
    ) {
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BroadcastListContent(
    state: BroadcastListState,
    onBroadcastClick: (String) -> Unit,
    onChannelClick: (ChannelList) -> Unit,
    onNotificationClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "방송 목록",
                    style = MaterialTheme.typography.headlineMedium
                )
            },
            actions = {
                IconButton(onClick = onNotificationClick) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "알림"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                actionIconContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )



        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onBroadcastClick("1") }
            ) {
                Text("방송 1로 이동")
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
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
}