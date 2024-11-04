package com.fm404.onair.features.auth.presentation.register.state

sealed class RegisterEvent {
    data class UsernameChanged(val username: String) : RegisterEvent()
    data class PasswordChanged(val password: String) : RegisterEvent()
    data class ConfirmPasswordChanged(val confirmPassword: String) : RegisterEvent()
    data class PhoneNumberChanged(val phoneNumber: String) : RegisterEvent()
    data class VerificationCodeChanged(val code: String) : RegisterEvent()
    object RequestVerificationCode : RegisterEvent()
    object VerifyPhoneNumber : RegisterEvent()
    object NextClicked : RegisterEvent()
    object RegisterClicked : RegisterEvent()
}