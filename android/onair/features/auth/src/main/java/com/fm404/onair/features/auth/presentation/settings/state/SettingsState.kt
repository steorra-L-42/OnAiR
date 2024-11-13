package com.fm404.onair.features.auth.presentation.settings.state

import com.fm404.onair.core.common.base.ErrorState
import com.fm404.onair.domain.model.auth.UserInfo

data class SettingsState(
    val isLoading: Boolean = false,
    val userInfo: UserInfo? = null,
    val error: ErrorState? = null,
    val showNicknameDialog: Boolean = false,
    val showImageDialog: Boolean = false,
    val newNickname: String = ""
)