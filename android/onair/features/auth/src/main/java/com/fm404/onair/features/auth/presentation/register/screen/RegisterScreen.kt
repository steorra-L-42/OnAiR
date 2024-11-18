package com.fm404.onair.features.auth.presentation.register.screen

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.fm404.onair.core.contract.auth.AuthNavigationContract
import com.fm404.onair.core.designsystem.theme.*
import com.fm404.onair.features.auth.presentation.register.RegisterViewModel
import com.fm404.onair.features.auth.presentation.register.state.RegisterEvent

private const val TAG = "RegisterScreen"
private const val REQUEST_PHONE_STATE_PERMISSION = 100

@Composable
private fun VerificationSection(
    verificationCode: String,
    onVerificationCodeChange: (String) -> Unit,
    remainingTimeSeconds: Int,
    maxAttempts: Int,
    currentAttempts: Int,
    verificationError: String?,
    onVerifyClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(0.8f)
    ) {
        ValidationTextField(
            value = verificationCode,
            onValueChange = { code ->
                if (code.all { it.isDigit() } && code.length <= 6) {
                    onVerificationCodeChange(code)
                }
            },
            label = "인증번호",
            error = verificationError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            filteringType = FilteringType.VERIFICATION_CODE
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "남은 시도: ${maxAttempts - currentAttempts}회",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )

            Text(
                text = "${remainingTimeSeconds / 60}:${String.format("%02d", remainingTimeSeconds % 60)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onVerifyClick,
            enabled = enabled && verificationCode.length == 6 &&
                    currentAttempts < maxAttempts,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .align(Alignment.CenterHorizontally)
        ) {
            Text("확인")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TopAppBar(
            title = { Text("회원가입") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = OnairBackground
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        ValidationTextField(
            value = state.phoneNumberForUI,
            onValueChange = { }, // 읽기 전용이라 { }
            label = "휴대전화 번호",
            enabled = false,
            error = state.phoneError,
            modifier = Modifier.fillMaxWidth(0.8f),
            filteringType = FilteringType.DEFAULT
        )

        if (state.isPhoneVerified) {
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "인증되었습니다.",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = OnairHighlight
            )
        }

        // SIM에서 전화번호 retrieve 성공시 표시
        if (!state.isPhoneVerified) {
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = { viewModel.onEvent(RegisterEvent.RequestVerificationCode) },
                enabled = !state.isVerificationCodeSent && state.phoneNumber.isNotBlank(),
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Text("인증요청")
            }
        }

        // 인증번호 입력란
        if (state.isVerificationCodeSent && !state.isPhoneVerified) {
            Spacer(modifier = Modifier.height(16.dp))
            VerificationSection(
                verificationCode = state.verificationCode,
                onVerificationCodeChange = { code ->
                    viewModel.onEvent(RegisterEvent.VerificationCodeChanged(code))
                },
                remainingTimeSeconds = state.remainingTimeSeconds,
                maxAttempts = state.maxVerificationAttempts,
                currentAttempts = state.verificationAttempts,
                verificationError = state.verificationError,
                onVerifyClick = { viewModel.onEvent(RegisterEvent.VerifyPhoneNumber) },
                enabled = true
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 회원가입 폼 (번호 인증 후 표시)
        if (state.isPhoneVerified) {
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ValidationTextField(
                    value = state.username,
                    onValueChange = { viewModel.onEvent(RegisterEvent.UsernameChanged(it)) },
                    label = "ID",
                    error = state.usernameError,
                    enabled = !state.isUserIdAvailable,
                    filteringType = FilteringType.USERNAME,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.onEvent(RegisterEvent.CheckUserIdAvailability) },
                    modifier = Modifier.fillMaxWidth(0.6f),
                    enabled = !state.isUserIdAvailable &&
                            state.username.isNotBlank() &&
                            !state.isCheckingUserId &&
                            state.usernameError == null
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

                if (state.isUserIdAvailable) {
                    Text(
                        text = "✓ 사용 가능한 아이디입니다.",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ValidationTextField(
                        value = state.nickname,
                        onValueChange = { viewModel.onEvent(RegisterEvent.NicknameChanged(it)) },
                        label = "닉네임",
                        error = state.nicknameError,
                        modifier = Modifier.fillMaxWidth(0.8f),
                        filteringType = FilteringType.NICKNAME
                    )
                }

                if (state.isUserIdAvailable &&
                    state.nickname.isNotBlank() &&
                    state.nicknameError == null
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    ValidationTextField(
                        value = state.password,
                        onValueChange = { viewModel.onEvent(RegisterEvent.PasswordChanged(it)) },
                        label = "비밀번호",
                        error = state.passwordError,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(0.8f),
                        filteringType = FilteringType.PASSWORD
                    )
                }

                if (state.isUserIdAvailable &&
                    state.nickname.isNotBlank() &&
                    state.nicknameError == null &&
                    state.password.isNotBlank() &&
                    state.passwordError == null
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    ValidationTextField(
                        value = state.confirmPassword,
                        onValueChange = { viewModel.onEvent(RegisterEvent.ConfirmPasswordChanged(it)) },
                        label = "비밀번호 확인",
                        error = state.confirmPasswordError,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(0.8f),
                        filteringType = FilteringType.PASSWORD
                    )
                }

                if (state.isUserIdAvailable &&
                    state.nickname.isNotBlank() &&
                    state.nicknameError == null &&
                    state.password.isNotBlank() &&
                    state.passwordError == null &&
                    state.confirmPassword.isNotBlank() &&
                    state.confirmPasswordError == null
                ) {
                    Button(
                        onClick = { viewModel.onEvent(RegisterEvent.RegisterClicked) },
                        modifier = Modifier.fillMaxWidth(0.6f)
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("회원가입")
                        }
                    }
                }
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
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OnSurface,
                unfocusedBorderColor = OnSurface.copy(alpha = 0.5f),
                focusedLabelColor = OnSurface,
                unfocusedLabelColor = OnSurface.copy(alpha = 0.5f),
                cursorColor = OnSurface
            ),

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
