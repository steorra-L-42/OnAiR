package com.fm404.onair.features.auth.presentation.settings

import android.net.Uri
import androidx.core.net.toFile
import androidx.lifecycle.viewModelScope
import com.fm404.onair.core.common.base.BaseViewModel
import com.fm404.onair.core.contract.auth.AuthNavigationContract
import com.fm404.onair.core.contract.broadcast.BroadcastNavigationContract
import com.fm404.onair.domain.exception.DomainException
import com.fm404.onair.domain.repository.auth.UserRepository
import com.fm404.onair.domain.usecase.auth.GetUserInfoUseCase
import com.fm404.onair.features.auth.presentation.settings.state.SettingsEvent
import com.fm404.onair.features.auth.presentation.settings.state.SettingsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
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
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            viewModelScope.launch(Dispatchers.Main) {
                when (throwable) {
                    is DomainException -> {
                        if (throwable.code == "C001") { // 401 Unauthorized
                            // 로그인 화면으로 이동
                            authNavigationContract.navigateToLogin()
                        } else {
                            setState {
                                copy(
                                    isLoading = false,
                                    error = throwable.message,
                                    errorCode = throwable.code
                                )
                            }
                        }
                    }
                    else -> {
                        setState {
                            copy(
                                isLoading = false,
                                error = throwable.message ?: "알 수 없는 오류가 발생했습니다",
                                errorCode = "UNKNOWN"
                            )
                        }
                    }
                }
            }
        }) {
            setState { copy(isLoading = true, error = null, errorCode = null) }

            getUserInfoUseCase()
                .onSuccess { userInfo ->
                    setState { copy(isLoading = false, userInfo = userInfo) }
                }
                .onFailure { exception ->
                    throw exception // CoroutineExceptionHandler에서 처리
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
                    when (exception) {
                        is DomainException -> {
                            setState {
                                copy(
                                    isLoading = false,
                                    error = exception.message,
                                    errorCode = exception.code
                                )
                            }
                        }
                        else -> {
                            setState {
                                copy(
                                    isLoading = false,
                                    error = exception.message ?: "로그아웃 중 오류가 발생했습니다.",
                                    errorCode = "UNKNOWN"
                                )
                            }
                        }
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
                    when (exception) {
                        is DomainException -> {
                            setState {
                                copy(
                                    isLoading = false,
                                    error = exception.message,
                                    errorCode = exception.code
                                )
                            }
                        }
                        else -> {
                            setState {
                                copy(
                                    isLoading = false,
                                    error = exception.message ?: "닉네임 변경 중 오류가 발생했습니다.",
                                    errorCode = "UNKNOWN"
                                )
                            }
                        }
                    }
                }
        }
    }

    private fun handleUpdateProfileImage(uri: Uri) {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }

            try {
                val file = uri.toFile()
                userRepository.updateProfileImage(file)
                    .onSuccess {
                        fetchUserInfo()
                    }
                    .onFailure { exception ->
                        when (exception) {
                            is DomainException -> {
                                setState {
                                    copy(
                                        isLoading = false,
                                        error = exception.message,
                                        errorCode = exception.code
                                    )
                                }
                            }
                            else -> {
                                setState {
                                    copy(
                                        isLoading = false,
                                        error = exception.message ?: "프로필 이미지 변경 중 오류가 발생했습니다.",
                                        errorCode = "UNKNOWN"
                                    )
                                }
                            }
                        }
                    }
            } catch (e: Exception) {
                setState {
                    copy(
                        isLoading = false,
                        error = "이미지 처리 중 오류가 발생했습니다.",
                        errorCode = "UNKNOWN"
                    )
                }
            }
        }
    }

    private fun handleBack() {
        authNavigationContract.navigateBack()
    }
}