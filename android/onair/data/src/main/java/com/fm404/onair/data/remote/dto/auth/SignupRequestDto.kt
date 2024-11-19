package com.fm404.onair.data.remote.dto.auth

data class SignupRequestDto(
    val nickname: String,
    val username: String,
    val password: String,
    val phoneNumber: String,
    val verification: String
)