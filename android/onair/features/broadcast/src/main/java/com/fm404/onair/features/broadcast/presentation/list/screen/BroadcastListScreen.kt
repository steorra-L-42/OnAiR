package com.fm404.onair.features.broadcast.presentation.list.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import androidx.navigation.NavHostController
import com.fm404.onair.core.designsystem.theme.OnairHighlight
import com.fm404.onair.core.designsystem.theme.pBold
import com.fm404.onair.core.designsystem.theme.pSemiBold
import com.fm404.onair.domain.model.broadcast.ChannelList
import com.fm404.onair.features.broadcast.R
import com.fm404.onair.features.broadcast.presentation.list.BroadcastListViewModel
import com.fm404.onair.features.broadcast.presentation.list.screen.component.ChannelItem
import com.fm404.onair.features.broadcast.presentation.list.state.BroadcastListState

//---- FULL CODE START ----
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
        // 화면 전체를 차지하는 Box
        Box(modifier = Modifier.fillMaxSize()) {
            // 컨텐츠 부분 호출
            BroadcastListContent(
                state = state,
                onBroadcastClick = onBroadcastClick,
                onChannelClick = { channel ->
                    onBroadcastClick(channel.channelUuid) // 채널 UUID로 이동
                },
//                onNotificationClick = viewModel::onNotificationClick // 알림 버튼 클릭 처리,
                onCreateClick = onCreateClick
            )

//            FloatingActionButton(
//                onClick = onCreateClick,
//                modifier = Modifier
//                    .align(Alignment.BottomEnd)
//                    .padding(16.dp)
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Add,
//                    contentDescription = "방송 만들기"
//                )
//            }

            // 로딩 상태 표시
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center) // 중앙에 로딩 표시
                )
            }

            // 에러 상태 표시
            state.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error, // 에러 색상 적용
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }
        }
    }
}

// 방송 목록 콘텐츠 표시
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BroadcastListContent(
    state: BroadcastListState,
    onBroadcastClick: (String) -> Unit,
    onChannelClick: (ChannelList) -> Unit,
    onCreateClick: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
//                .background(OnairHighlight)
                .padding(horizontal = 11.dp, vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "방송 목록",
                fontFamily = pSemiBold,
                fontSize = 28.sp,
                color = Color.White,
                modifier = Modifier.weight(1f).padding(start = 16.dp)
            )
            
//            Spacer(modifier = Modifier.weight(4f))
            
            IconButton(
//                onClick = onNotificationClick
                onClick = onCreateClick
            ) {
                Image(
                    painter = painterResource(id = R.drawable.add_mic),
                    contentDescription = "채널 추가",
                    modifier = Modifier.size(27.dp)
                )
            }
        }


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
