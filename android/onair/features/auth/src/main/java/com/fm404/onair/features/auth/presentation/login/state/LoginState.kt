package com.fm404.onair.features.auth.presentation.login.state

data class LoginState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)