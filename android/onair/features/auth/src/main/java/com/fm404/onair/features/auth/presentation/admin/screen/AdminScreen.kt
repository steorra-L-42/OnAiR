package com.fm404.onair.features.auth.presentation.admin.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fm404.onair.features.auth.presentation.admin.AdminViewModel
import com.fm404.onair.features.auth.presentation.admin.state.AdminEvent
import com.fm404.onair.features.auth.presentation.admin.state.AdminState

@Composable
fun AdminScreen(
    viewModel: AdminViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    AdminContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminContent(
    state: AdminState,
    onEvent: (AdminEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            title = { Text("관리자 페이지") },
//            navigationIcon = {
//                IconButton(onClick = { onEvent(AdminEvent.OnBackClick) }) {
//                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                }
//            },
            modifier = Modifier.fillMaxWidth()
        )

        if (state.isLoading) {
            CircularProgressIndicator()
        } else {
            Text(
                text = "관리자 페이지",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        state.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}