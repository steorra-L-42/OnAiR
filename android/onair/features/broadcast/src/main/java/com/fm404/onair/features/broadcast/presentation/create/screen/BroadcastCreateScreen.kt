package com.fm404.onair.features.broadcast.presentation.create.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fm404.onair.domain.model.broadcast.CreateChannelPlayList
import com.fm404.onair.features.broadcast.presentation.create.BroadcastCreateViewModel
import com.fm404.onair.features.broadcast.presentation.create.state.BroadcastCreateEvent
import com.fm404.onair.features.broadcast.presentation.create.state.BroadcastCreateState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BroadcastCreateScreen(
    onBackClick: () -> Unit,
    viewModel: BroadcastCreateViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    BroadcastCreateContent(
        state = state,
        onTtsEngineChange = { viewModel.onEvent(BroadcastCreateEvent.OnTtsEngineChange(it)) },
        onPersonalityChange = { viewModel.onEvent(BroadcastCreateEvent.OnPersonalityChange(it)) },
        onTopicChange = { viewModel.onEvent(BroadcastCreateEvent.OnTopicChange(it)) },
        onPlayListChange = { viewModel.onEvent(BroadcastCreateEvent.OnPlayListChange(it)) },
        onCreateClick = { viewModel.onEvent(BroadcastCreateEvent.OnCreateClick) },
        onBackClick = onBackClick,
        onErrorDismiss = { viewModel.onErrorDismiss() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BroadcastCreateContent(
    state: BroadcastCreateState,
    onTtsEngineChange: (String) -> Unit,
    onPersonalityChange: (String) -> Unit,
    onTopicChange: (String) -> Unit,
    onPlayListChange: (List<CreateChannelPlayList>) -> Unit,
    onCreateClick: () -> Unit,
    onBackClick: () -> Unit,
    onErrorDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopAppBar(
            title = { Text("방송 만들기") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // TTS 엔진
        OutlinedTextField(
            value = state.ttsEngine,
            onValueChange = onTtsEngineChange,
            label = { Text("TTS 엔진") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 성격
        OutlinedTextField(
            value = state.personality,
            onValueChange = onPersonalityChange,
            label = { Text("성격") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 뉴스 주제
        OutlinedTextField(
            value = state.topic,
            onValueChange = onTopicChange,
            label = { Text("뉴스 주제") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        // 플레이리스트 버튼
        Button(
            onClick = { /* TODO: 플레이리스트 추가 화면으로 이동 */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("플레이리스트 추가")
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onCreateClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            enabled = state.personality.isNotBlank() &&
                    state.topic.isNotBlank() &&
                    !state.isLoading
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.surface
                )
            } else {
                Text("방송 만들기")
            }
        }
    }

    state.error?.let { error ->
        AlertDialog(
            onDismissRequest = onErrorDismiss,
            title = { Text("오류") },
            text = { Text(error) },
            confirmButton = {
                TextButton(onClick = onErrorDismiss) {
                    Text("확인")
                }
            }
        )
    }
}