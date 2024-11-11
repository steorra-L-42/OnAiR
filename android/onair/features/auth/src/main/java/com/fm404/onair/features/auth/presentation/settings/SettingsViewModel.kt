package com.fm404.onair.features.auth.presentation.settings

import android.net.Uri
import androidx.core.net.toFile
import androidx.lifecycle.viewModelScope
import com.fm404.onair.core.common.base.BaseViewModel
import com.fm404.onair.core.contract.auth.AuthNavigationContract
import com.fm404.onair.core.contract.broadcast.BroadcastNavigationContract
import com.fm404.onair.domain.repository.auth.UserRepository
import com.fm404.onair.domain.usecase.auth.GetUserInfoUseCase
import com.fm404.onair.features.auth.presentation.settings.state.SettingsEvent
import com.fm404.onair.features.auth.presentation.settings.state.SettingsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authNavigationContract: AuthNavigationContract,
    private val broadcastNavigationContract: BroadcastNavigationContract,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val userRepository: UserRepository
) : BaseViewModel<SettingsState, SettingsEvent>() {

    override fun createInitialState(): SettingsState = SettingsState()

    init {
        onEvent(SettingsEvent.FetchUserInfo)
    }

    override fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.OnLogoutClick -> handleLogout()
            is SettingsEvent.OnBackClick -> handleBack()
            is SettingsEvent.FetchUserInfo -> fetchUserInfo()
            is SettingsEvent.OnShowNicknameDialog -> setState { copy(showNicknameDialog = true, newNickname = userInfo?.nickname ?: "") }
            is SettingsEvent.OnHideNicknameDialog -> setState { copy(showNicknameDialog = false) }
            is SettingsEvent.OnNicknameChange -> setState { copy(newNickname = event.nickname) }
            is SettingsEvent.OnUpdateNickname -> handleUpdateNickname()
            is SettingsEvent.OnImageSelected -> handleUpdateProfileImage(event.uri)
            is SettingsEvent.OnShowImageDialog -> setState { copy(showImageDialog = true) }
            is SettingsEvent.OnHideImageDialog -> setState { copy(showImageDialog = false) }
        }
    }

    private fun fetchUserInfo() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }

            getUserInfoUseCase()
                .onSuccess { userInfo ->
                    setState { copy(isLoading = false, userInfo = userInfo) }
                }
                .onFailure { exception ->
                    setState {
                        copy(
                            isLoading = false,
                            error = exception.message ?: "사용자 정보를 불러오는데 실패했습니다."
                        )
                    }
                }
        }
    }

    private fun handleLogout() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }

            userRepository.logout()
                .onSuccess {
                    broadcastNavigationContract.navigateToBroadcastList()
                }
                .onFailure { exception ->
                    setState {
                        copy(
                            isLoading = false,
                            error = exception.message ?: "로그아웃 중 오류가 발생했습니다."
                        )
                    }
                }
        }
    }

    private fun handleUpdateNickname() {
        val newNickname = uiState.value.newNickname
        if (newNickname.isBlank() || newNickname.length > 40) {
            setState { copy(error = "닉네임은 1~40자 사이여야 합니다.") }
            return
        }

        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }

            userRepository.updateNickname(newNickname)
                .onSuccess {
                    setState { copy(showNicknameDialog = false) }
                    fetchUserInfo()
                }
                .onFailure { exception ->
                    setState {
                        copy(
                            isLoading = false,
                            error = exception.message ?: "닉네임 변경 중 오류가 발생했습니다."
                        )
                    }
                }
        }
    }

    private fun handleUpdateProfileImage(uri: Uri) {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }

            try {
                val file = uri.toFile() // 실제 구현에서는 ContentResolver를 사용하여 파일로 변환
                userRepository.updateProfileImage(file)
                    .onSuccess {
                        fetchUserInfo()
                    }
                    .onFailure { exception ->
                        setState {
                            copy(
                                isLoading = false,
                                error = exception.message ?: "프로필 이미지 변경 중 오류가 발생했습니다."
                            )
                        }
                    }
            } catch (e: Exception) {
                setState {
                    copy(
                        isLoading = false,
                        error = "이미지 처리 중 오류가 발생했습니다."
                    )
                }
            }
        }
    }

    private fun handleBack() {
        authNavigationContract.navigateBack()
    }
}