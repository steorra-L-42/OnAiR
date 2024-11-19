package com.fm404.onair.features.auth.presentation.settings.state

import android.net.Uri

sealed class SettingsEvent {
    object OnLogoutClick : SettingsEvent()
    object OnBackClick : SettingsEvent()
    object FetchUserInfo : SettingsEvent()

    object OnShowNicknameDialog : SettingsEvent()
    object OnHideNicknameDialog : SettingsEvent()
    data class OnNicknameChange(val nickname: String) : SettingsEvent()
    object OnUpdateNickname : SettingsEvent()

    data class OnImageSelected(val uri: Uri) : SettingsEvent()
    object OnShowImageDialog : SettingsEvent()
    object OnHideImageDialog : SettingsEvent()


    data class ShowToast(val message: String) : SettingsEvent()
    object ClearError : SettingsEvent()
}