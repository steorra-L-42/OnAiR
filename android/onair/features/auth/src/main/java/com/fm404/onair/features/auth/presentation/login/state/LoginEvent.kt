package com.fm404.onair.features.auth.presentation.login.state

sealed class LoginEvent {
    data class UsernameChanged(val username: String) : LoginEvent()
    data class PasswordChanged(val password: String) : LoginEvent()
    object LoginClicked : LoginEvent()
    object RegisterClicked : LoginEvent()
}