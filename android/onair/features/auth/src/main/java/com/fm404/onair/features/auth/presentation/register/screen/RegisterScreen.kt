package com.fm404.onair.features.auth.presentation.register.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.fm404.onair.features.auth.presentation.register.RegisterViewModel
import com.fm404.onair.features.auth.presentation.register.state.RegisterState
import com.fm404.onair.features.auth.presentation.register.state.RegisterEvent

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    RegisterContent(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateBack = { navController.navigateUp() }
    )
}

@Composable
private fun RegisterContent(
    state: RegisterState,
    onEvent: (RegisterEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "회원가입",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 휴대전화 인증 섹션
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = state.phoneNumber,
                onValueChange = { onEvent(RegisterEvent.PhoneNumberChanged(it)) },
                label = { Text("휴대전화 번호") },
                modifier = Modifier.weight(1f),
                enabled = !state.isPhoneVerified,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                    disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                    disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            )

            if (!state.isPhoneVerified) {
                Button(
                    onClick = { onEvent(RegisterEvent.RequestVerificationCode) },
                    enabled = !state.isVerificationCodeSent && state.phoneNumber.length == 11
                ) {
                    Text("인증요청")
                }
            }
        }

        if (state.isVerificationCodeSent && !state.isPhoneVerified) {
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = state.verificationCode,
                    onValueChange = { onEvent(RegisterEvent.VerificationCodeChanged(it)) },
                    label = { Text("인증번호") },
                    modifier = Modifier.weight(1f),
                    enabled = state.verificationAttempts < state.maxVerificationAttempts
                )

                Text(
                    text = "${state.remainingTimeSeconds / 60}:${String.format("%02d", state.remainingTimeSeconds % 60)}",
                    color = MaterialTheme.colorScheme.error
                )

                Button(
                    onClick = { onEvent(RegisterEvent.VerifyPhoneNumber) },
                    enabled = state.verificationAttempts < state.maxVerificationAttempts &&
                            state.verificationCode.length == 6
                ) {
                    Text("확인")
                }
            }

            if (state.verificationAttempts > 0) {
                Text(
                    text = "인증 시도: ${state.verificationAttempts}/${state.maxVerificationAttempts}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }

        if (state.isPhoneVerified) {
            Text(
                text = "✓ 휴대전화 인증이 완료되었습니다.",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 기본 정보 입력 (인증 완료 후에만 표시)
            OutlinedTextField(
                value = state.username,
                onValueChange = { onEvent(RegisterEvent.UsernameChanged(it)) },
                label = { Text("ID") },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.password,
                onValueChange = { onEvent(RegisterEvent.PasswordChanged(it)) },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.confirmPassword,
                onValueChange = { onEvent(RegisterEvent.ConfirmPasswordChanged(it)) },
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 다음/회원가입 버튼
        Button(
            onClick = {
                if (state.isPhoneVerified) {
                    onEvent(RegisterEvent.RegisterClicked)
                } else {
                    onEvent(RegisterEvent.NextClicked)
                }
            },
            enabled = if (state.isPhoneVerified) {
                // 회원가입 버튼 활성화 조건
                state.username.isNotBlank() &&
                        state.password.isNotBlank() &&
                        state.confirmPassword.isNotBlank()
            } else {
                // 다음 버튼 활성화 조건
                state.isPhoneVerified
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(if (state.isPhoneVerified) "회원가입" else "다음")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onNavigateBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("로그인으로 돌아가기")
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