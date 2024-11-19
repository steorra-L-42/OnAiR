package com.fm404.onair.domain.model.auth

data class LoginRequest(
    val username: String,
    val password: String
)