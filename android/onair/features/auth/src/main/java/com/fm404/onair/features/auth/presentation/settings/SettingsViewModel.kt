package com.fm404.onair.features.auth.presentation.settings

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.lifecycle.viewModelScope
import com.fm404.onair.core.common.base.BaseViewModel
import com.fm404.onair.core.contract.auth.AuthNavigationContract
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
import com.fm404.onair.core.common.base.ErrorState

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val application: Application,
    val authNavigationContract: AuthNavigationContract,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val userRepository: UserRepository
) : BaseViewModel<SettingsState, SettingsEvent>() {

    override fun createInitialState(): SettingsState = SettingsState()

    init {
        onEvent(SettingsEvent.FetchUserInfo)
    }

    private fun handleError(exception: Throwable) {
        when (exception) {
            is DomainException -> {
                setState {
                    copy(
                        isLoading = false,
                        error = ErrorState(
                            code = exception.code,
                            message = exception.message
                        )
                    )
                }
            }
            else -> {
                setState {
                    copy(
                        isLoading = false,
                        error = ErrorState(
                            code = "999",
                            message = exception.message ?: "알 수 없는 오류가 발생했습니다"
                        )
                    )
                }
            }
        }
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
        viewModelScope.launch(Dispatchers.IO) {
            setState { copy(isLoading = true, error = null) }

            getUserInfoUseCase()
                .onSuccess { userInfo ->
                    setState { copy(isLoading = false, userInfo = userInfo) }
                }
                .onFailure { exception ->
                    handleError(exception)
                }
        }
    }

    private fun handleLogout() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }

            try {
                // 토큰 제거 시도
                userRepository.logout()
                    .onSuccess {
                        Log.d("Settings", "로그아웃 성공")
                    }
                    .onFailure { exception ->
                        // 실패해도 무시
                        Log.d("Settings", "로그아웃 실패: ${exception.message}")
                    }

                // 결과와 상관없이 로그인 화면으로 이동
                Log.d("Settings", "로그인 화면으로 이동")
                authNavigationContract.navigateToLogin()

            } catch (e: Exception) {
                // 예상치 못한 에러도 무시하고 로그인 화면으로
                Log.e("Settings", "예상치 못한 오류", e)
                authNavigationContract.navigateToLogin()
            }
        }
    }

    private fun handleUpdateNickname() {
        val newNickname = uiState.value.newNickname
        if (newNickname.isBlank() || newNickname.length > 40) {
            setState { copy(error = ErrorState("NICKNAME_ERROR", "닉네임은 1~40자 사이여야 합니다.")) }
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
                    handleError(exception)
                }
        }
    }

    private fun handleUpdateProfileImage(uri: Uri) {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }

            try {
                // ContentResolver를 사용하여 실제 파일로 변환
                val contentResolver = application.contentResolver
                val inputStream = contentResolver.openInputStream(uri)
                val file = createTempFile("profile", ".jpg").apply {
                    outputStream().use { fileOut ->
                        inputStream?.copyTo(fileOut)
                    }
                }
                inputStream?.close()

                userRepository.updateProfileImage(file)
                    .onSuccess { response ->
                        // 임시 파일 삭제
                        file.delete()
                        // 프로필 업데이트 성공 후 유저 정보 갱신
                        fetchUserInfo()
                    }
                    .onFailure { exception ->
                        // 임시 파일 삭제
                        file.delete()
                        handleError(exception)
                    }
            } catch (e: Exception) {
                setState {
                    copy(
                        isLoading = false,
                        error = ErrorState("IMAGE_ERROR", "이미지 처리 중 오류가 발생했습니다: ${e.message}")
                    )
                }
            }
        }
    }

    private fun handleBack() {
        authNavigationContract.navigateBack()
    }
}