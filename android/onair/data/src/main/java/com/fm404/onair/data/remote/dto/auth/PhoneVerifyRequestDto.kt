package com.fm404.onair.data.remote.dto.auth

data class PhoneVerifyRequestDto(
    val phoneNumber: String,
    val verification: String
)