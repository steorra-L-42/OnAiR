package com.fm404.onair.features.auth.presentation.login.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.fm404.onair.core.designsystem.theme.*
import com.fm404.onair.core.common.R
import com.fm404.onair.features.auth.presentation.login.LoginViewModel
import com.fm404.onair.features.auth.presentation.login.state.LoginState
import com.fm404.onair.features.auth.presentation.login.state.LoginEvent

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.setNavController(navController)
    }

    LoginContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun LoginContent(
    state: LoginState,
    onEvent: (LoginEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_onair),
            contentDescription = "OnAir Logo",
            modifier = Modifier.size(160.dp)
        )

        OutlinedTextField(
            value = state.username,
            onValueChange = { onEvent(LoginEvent.UsernameChanged(it)) },
            label = { Text("ID") },
            modifier = Modifier.width(300.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OnSurface,
                unfocusedBorderColor = OnSurface.copy(alpha = 0.5f),
                focusedLabelColor = OnSurface,
                unfocusedLabelColor = OnSurface.copy(alpha = 0.5f),
                cursorColor = OnSurface
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = { onEvent(LoginEvent.PasswordChanged(it)) },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.width(300.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OnSurface,
                unfocusedBorderColor = OnSurface.copy(alpha = 0.5f),
                focusedLabelColor = OnSurface,
                unfocusedLabelColor = OnSurface.copy(alpha = 0.5f),
                cursorColor = OnSurface
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onEvent(LoginEvent.LoginClicked) },
            modifier = Modifier.width(240.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = OnBackground,
                contentColor = OnSecondary
            )
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = OnSecondary
                )
            } else {
                Text("Login")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 회원가입 버튼 추가
        TextButton(
            onClick = { onEvent(LoginEvent.RegisterClicked) },
            modifier = Modifier.width(300.dp)
        ) {
            Text(
                "아직 계정이 없으신가요? 회원가입",
                color = OnPrimary
            )
        }

        if (state.error != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = state.error,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}