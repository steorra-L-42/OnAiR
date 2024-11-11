package com.fm404.onair.features.auth.presentation.settings

import androidx.lifecycle.viewModelScope
import com.fm404.onair.core.common.base.BaseViewModel
import com.fm404.onair.core.contract.auth.AuthNavigationContract
import com.fm404.onair.core.contract.broadcast.BroadcastNavigationContract
import com.fm404.onair.domain.repository.auth.UserRepository
import com.fm404.onair.features.auth.presentation.settings.state.SettingsEvent
import com.fm404.onair.features.auth.presentation.settings.state.SettingsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authNavigationContract: AuthNavigationContract,
    private val broadcastNavigationContract: BroadcastNavigationContract,
    private val userRepository: UserRepository
) : BaseViewModel<SettingsState, SettingsEvent>() {

    override fun createInitialState(): SettingsState = SettingsState()

    override fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.OnLogoutClick -> handleLogout()
            is SettingsEvent.OnEditProfileClick -> handleEditProfile()
            is SettingsEvent.OnBackClick -> handleBack()
            is SettingsEvent.OnUserNameChange -> updateUserName(event.name)
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

    private fun handleEditProfile() {
        // 프로필 수정 로직
        setState {
            this.copy(isLoading = true)
        }
        // 프로필 수정 API 호출 등의 로직
        setState {
            this.copy(isLoading = false)
        }
    }

    private fun handleBack() {
        authNavigationContract.navigateBack()
    }

    private fun updateUserName(name: String) {
        setState {
            this.copy(userName = name)
        }
    }
}