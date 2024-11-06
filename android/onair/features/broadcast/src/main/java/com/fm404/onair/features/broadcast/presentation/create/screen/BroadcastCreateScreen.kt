package com.fm404.onair.features.broadcast.presentation.create.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
        onTitleChange = { viewModel.onEvent(BroadcastCreateEvent.OnTitleChange(it)) },
        onDescriptionChange = { viewModel.onEvent(BroadcastCreateEvent.OnDescriptionChange(it)) },
        onCreateClick = { viewModel.onEvent(BroadcastCreateEvent.OnCreateClick) },
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BroadcastCreateContent(
    state: BroadcastCreateState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCreateClick: () -> Unit,
    onBackClick: () -> Unit
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

        OutlinedTextField(
            value = state.title,
            onValueChange = onTitleChange,
            label = { Text("방송 제목") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.description,
            onValueChange = onDescriptionChange,
            label = { Text("방송 설명") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 5
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onCreateClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            enabled = state.title.isNotBlank() && !state.isLoading
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

    // Error Dialog
    state.error?.let { error ->
        AlertDialog(
            onDismissRequest = { /* TODO: Error 상태 초기화 */ },
            title = { Text("오류") },
            text = { Text(error) },
            confirmButton = {
                TextButton(onClick = { /* TODO: Error 상태 초기화 */ }) {
                    Text("확인")
                }
            }
        )
    }
}