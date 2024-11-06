package com.fm404.onair.features.auth.presentation.settings.state

sealed class SettingsEvent {
    object OnLogoutClick : SettingsEvent()
    object OnEditProfileClick : SettingsEvent()
    object OnBackClick : SettingsEvent()
    data class OnUserNameChange(val name: String) : SettingsEvent()
}