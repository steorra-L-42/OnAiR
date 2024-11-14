package com.fm404.onair.features.auth.presentation.register.screen

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.fm404.onair.core.contract.auth.AuthNavigationContract
import com.fm404.onair.features.auth.presentation.register.RegisterViewModel
import com.fm404.onair.features.auth.presentation.register.state.RegisterEvent

private const val TAG = "RegisterScreen"
private const val REQUEST_PHONE_STATE_PERMISSION = 100

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
            == PackageManager.PERMISSION_GRANTED) {
            viewModel.retrievePhoneNumber(context)
        } else {
            ActivityCompat.requestPermissions(
                (context as Activity),
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                REQUEST_PHONE_STATE_PERMISSION
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick =  {
                navController.popBackStack()
                Unit
            }
        ) {
            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
        }

        Text(
            text = "회원가입",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        ValidationTextField(
            value = state.phoneNumberForUI,
            onValueChange = { }, // 읽기 전용이라 { }
            label = "휴대전화 번호",
            enabled = false,
            error = state.phoneError,
            modifier = Modifier.fillMaxWidth(),
            filteringType = FilteringType.DEFAULT
        )

        // SIM에서 전화번호 retrieve 성공시 표시
        if (!state.isPhoneVerified) {
            Button(
                onClick = { viewModel.onEvent(RegisterEvent.RequestVerificationCode) },
                enabled = !state.isVerificationCodeSent && state.phoneNumber.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("인증요청")
            }
        }

        // 인증번호 입력란
        if (state.isVerificationCodeSent && !state.isPhoneVerified) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ValidationTextField(
                    value = state.verificationCode,
                    onValueChange = { code ->
                        if (code.all { it.isDigit() } && code.length <= 6) {
                            viewModel.onEvent(RegisterEvent.VerificationCodeChanged(code))
                        }
                    },
                    label = "인증번호",
                    error = state.verificationError,
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    filteringType = FilteringType.VERIFICATION_CODE
                )

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${state.remainingTimeSeconds / 60}:${String.format("%02d", state.remainingTimeSeconds % 60)}",
                        color = MaterialTheme.colorScheme.error
                    )

                    Text(
                        text = "남은 시도: ${state.maxVerificationAttempts - state.verificationAttempts}회",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                Button(
                    onClick = { viewModel.onEvent(RegisterEvent.VerifyPhoneNumber) },
                    enabled = state.verificationCode.length == 6 &&
                            state.verificationAttempts < state.maxVerificationAttempts,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("확인")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 회원가입 폼 (번호 인증 후 표시)
        if (state.isPhoneVerified) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ValidationTextField(
                    value = state.username,
                    onValueChange = { viewModel.onEvent(RegisterEvent.UsernameChanged(it)) },
                    label = "ID",
                    error = state.usernameError,
                    enabled = !state.isUserIdAvailable,
                    modifier = Modifier.weight(1f),
                    filteringType = FilteringType.USERNAME
                )

                Button(
                    onClick = { viewModel.onEvent(RegisterEvent.CheckUserIdAvailability) },
                    enabled = !state.isUserIdAvailable && state.username.isNotBlank() && !state.isCheckingUserId
                ) {
                    if (state.isCheckingUserId) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("중복확인")
                    }
                }
            }

            if (state.isUserIdAvailable) {
                Text(
                    text = "✓ 사용 가능한 아이디입니다.",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            ValidationTextField(
                value = state.nickname,
                onValueChange = { viewModel.onEvent(RegisterEvent.NicknameChanged(it)) },
                label = "닉네임",
                error = state.nicknameError,
                modifier = Modifier.fillMaxWidth(),
                filteringType = FilteringType.NICKNAME
            )

            Spacer(modifier = Modifier.height(8.dp))

            ValidationTextField(
                value = state.password,
                onValueChange = { viewModel.onEvent(RegisterEvent.PasswordChanged(it)) },
                label = "비밀번호",
                error = state.passwordError,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                filteringType = FilteringType.PASSWORD
            )

            Spacer(modifier = Modifier.height(8.dp))

            ValidationTextField(
                value = state.confirmPassword,
                onValueChange = { viewModel.onEvent(RegisterEvent.ConfirmPasswordChanged(it)) },
                label = "비밀번호 확인",
                error = state.confirmPasswordError,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                filteringType = FilteringType.PASSWORD
            )
        }

        // Buttons Section
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (state.isPhoneVerified) {
                    viewModel.onEvent(RegisterEvent.RegisterClicked)
                } else {
                    viewModel.onEvent(RegisterEvent.NextClicked)
                }
            },
            enabled = if (state.isPhoneVerified) {
                state.username.isNotBlank() && state.isUserIdAvailable &&
                        state.password.isNotBlank() && state.confirmPassword.isNotBlank() &&
                        state.nickname.isNotBlank() &&
                        state.usernameError == null && state.passwordError == null &&
                        state.confirmPasswordError == null && state.nicknameError == null
            } else {
                Log.d(TAG, "RegisterScreen: isPhoneVerified FALSE")
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

        // Display general error if any
        if (state.generalError != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = state.generalError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun ValidationTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    filteringType: FilteringType
) {
    // 비밀번호 규칙에 따라 input 자체를 필터링함
    val regex = when (filteringType) {
        FilteringType.USERNAME -> "[a-z0-9]".toRegex()
        FilteringType.PASSWORD -> "[A-Za-z\\d!@#%^&*()\\-_=+\\[\\]{}|;:,<.>?]".toRegex()
        FilteringType.NICKNAME -> "^[ㄱ-ㅎ가-힣a-zA-Z0-9ㆍᆢ]{0,24}$".toRegex()
        FilteringType.VERIFICATION_CODE -> "\\d".toRegex()
        FilteringType.DEFAULT -> ".*".toRegex()
    }


    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                val filteredValue = it.filter { char -> regex.matches(char.toString()) }
                onValueChange(filteredValue)
            },
            label = { Text(label) },
            isError = error != null,
            enabled = enabled,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

enum class FilteringType {
    USERNAME, PASSWORD, NICKNAME, VERIFICATION_CODE, DEFAULT
}
