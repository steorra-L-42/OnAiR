package com.fm404.onair.features.broadcast.presentation.create.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fm404.onair.core.common.util.BroadcastConstants
import com.fm404.onair.domain.model.broadcast.CreateChannelPlayList
import com.fm404.onair.features.broadcast.R
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
        onNewsTopicChange = { viewModel.onEvent(BroadcastCreateEvent.OnNewsTopicChange(it)) },
        onChannelNameChange = { viewModel.onEvent(BroadcastCreateEvent.OnChannelNameChange(it)) },
        onTrackListChange = { viewModel.onEvent(BroadcastCreateEvent.OnTrackListChange(it)) },
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
    onNewsTopicChange: (String) -> Unit,
    onChannelNameChange: (String) -> Unit,
    onTrackListChange: (List<CreateChannelPlayList>) -> Unit,
    onCreateClick: () -> Unit,
    onBackClick: () -> Unit,
    onErrorDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
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

        // 채널 이름
        OutlinedTextField(
            value = state.channelName,
            onValueChange = onChannelNameChange,
            label = { Text("채널 이름") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // TTS 엔진 선택
        var ttsExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = ttsExpanded,
            onExpandedChange = { ttsExpanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = BroadcastConstants.TTS_ENGINE_OPTIONS[state.ttsEngine] ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("TTS 엔진") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = ttsExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = ttsExpanded,
                onDismissRequest = { ttsExpanded = false }
            ) {
                BroadcastConstants.TTS_ENGINE_OPTIONS.forEach { (key, value) ->
                    DropdownMenuItem(
                        text = { Text(value) },
                        onClick = {
                            onTtsEngineChange(key)
                            ttsExpanded = false
                        }
                    )
                }
            }
        }

        // 썸네일 이미지 표시
        if (state.ttsEngine.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .size(250.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(
                            id = when (state.ttsEngine) {
                                "TYPECAST_SENA" -> com.fm404.onair.core.common.R.drawable.sena
                                "TYPECAST_JEROME" -> com.fm404.onair.core.common.R.drawable.jerome
                                "TYPECAST_HYEONJI" -> com.fm404.onair.core.common.R.drawable.hyunji
                                "TYPECAST_EUNBIN" -> com.fm404.onair.core.common.R.drawable.eunbin
                                else -> com.fm404.onair.core.common.R.drawable.sena // 기본 이미지
                            }
                        ),
                        contentDescription = "DJ 썸네일",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 성격 선택
        var personalityExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = personalityExpanded,
            onExpandedChange = { personalityExpanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = BroadcastConstants.PERSONALITY_OPTIONS[state.personality] ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("성격") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = personalityExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = personalityExpanded,
                onDismissRequest = { personalityExpanded = false }
            ) {
                BroadcastConstants.PERSONALITY_OPTIONS.forEach { (key, value) ->
                    DropdownMenuItem(
                        text = { Text(value) },
                        onClick = {
                            onPersonalityChange(key)
                            personalityExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 뉴스 주제 선택
        var newsTopicExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = newsTopicExpanded,
            onExpandedChange = { newsTopicExpanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = BroadcastConstants.NEWS_TOPIC_OPTIONS[state.newsTopic] ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("뉴스 주제") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = newsTopicExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = newsTopicExpanded,
                onDismissRequest = { newsTopicExpanded = false }
            ) {
                BroadcastConstants.NEWS_TOPIC_OPTIONS.forEach { (key, value) ->
                    DropdownMenuItem(
                        text = { Text(value) },
                        onClick = {
                            onNewsTopicChange(key)
                            newsTopicExpanded = false
                        }
                    )
                }
            }
        }

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
            enabled = state.channelName.isNotBlank() &&
                    state.ttsEngine.isNotBlank() &&
                    state.personality.isNotBlank() &&
                    state.newsTopic.isNotBlank() &&
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