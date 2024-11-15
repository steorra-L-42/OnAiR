package com.fm404.onair.features.broadcast.presentation.story.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fm404.onair.features.broadcast.presentation.story.StoryViewModel
import com.fm404.onair.features.broadcast.presentation.story.state.StoryEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryScreen(
    broadcastId: String,
    onBackClick: () -> Unit,
    viewModel: StoryViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val selectedMusic = state.selectedMusic
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect {
            onBackClick()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("사연 작성") },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "뒤로가기"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = state.title,
                    onValueChange = { viewModel.onEvent(StoryEvent.OnTitleChange(it)) },
                    label = { Text("제목") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = state.content,
                    onValueChange = { viewModel.onEvent(StoryEvent.OnContentChange(it)) },
                    label = { Text("내용") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    maxLines = 10
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        // TODO: 음악 선택 다이얼로그 표시
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (selectedMusic != null) "음악 변경하기" else "음악 선택하기")
                }

                selectedMusic?.let { music ->
                    Text(
                        text = "${music.musicArtist} - ${music.musicTitle}",
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { viewModel.onEvent(StoryEvent.OnSubmit) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.title.isNotBlank() && state.content.isNotBlank() && !state.isLoading
                ) {
                    Text("사연 보내기")
                }
            }
        }

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        state.error?.let { error ->
            LaunchedEffect(error) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
        }
    }
}