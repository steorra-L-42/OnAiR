package com.fm404.onair.features.auth.presentation.settings.state

data class SettingsState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val userName: String = "",
    val profileImage: String? = null
)