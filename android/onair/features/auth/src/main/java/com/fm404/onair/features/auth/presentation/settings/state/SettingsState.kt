package com.fm404.onair.features.auth.presentation.settings.state

import android.net.Uri
import com.fm404.onair.domain.model.auth.UserInfo

data class SettingsState(
    val userInfo: UserInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val errorCode: String? = null,
    val showNicknameDialog: Boolean = false,
    val showImageDialog: Boolean = false,
    val newNickname: String = "",
    val selectedImageUri: Uri? = null
)