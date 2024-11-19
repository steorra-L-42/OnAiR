package com.fm404.onair.domain.model.auth

data class RegisterRequest(
    val nickname: String,
    val username: String,
    val password: String,
    val phoneNumber: String,
    val verificationCode: String
)