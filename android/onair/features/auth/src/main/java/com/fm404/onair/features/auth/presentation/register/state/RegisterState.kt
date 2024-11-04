package com.fm404.onair.features.auth.presentation.register.state

data class RegisterState(
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val phoneNumber: String = "",
    val verificationCode: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isPhoneVerified: Boolean = false,
    val isVerificationCodeSent: Boolean = false,
    val remainingTimeSeconds: Int = 180, // 3분
    val verificationAttempts: Int = 0,   // 인증 시도 횟수
    val maxVerificationAttempts: Int = 5  // 최대 시도 횟수
)