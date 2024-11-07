package com.fm404.onair.features.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fm404.onair.domain.model.auth.LoginRequest
import com.fm404.onair.domain.model.auth.RegisterRequest
import com.fm404.onair.domain.repository.auth.UserRepository
import com.fm404.onair.features.auth.presentation.register.state.RegisterEvent
import com.fm404.onair.features.auth.presentation.register.state.RegisterState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    private var verificationTimer: Job? = null

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.UsernameChanged -> {
                _state.value = _state.value.copy(
                    username = event.username,
                    error = null
                )
            }
            is RegisterEvent.PasswordChanged -> {
                _state.value = _state.value.copy(
                    password = event.password,
                    error = null
                )
            }
            is RegisterEvent.ConfirmPasswordChanged -> {
                _state.value = _state.value.copy(
                    confirmPassword = event.confirmPassword,
                    error = null
                )
            }
            is RegisterEvent.NicknameChanged -> {
                _state.value = _state.value.copy(
                    nickname = event.nickname,
                    error = null
                )
            }
            is RegisterEvent.CheckUserIdAvailability -> {
                checkUserIdAvailability()
            }
            is RegisterEvent.PhoneNumberChanged -> {
                if (!_state.value.isPhoneVerified) {
                    _state.value = _state.value.copy(
                        phoneNumber = event.phoneNumber,
                        error = null
                    )
                }
            }
            is RegisterEvent.VerificationCodeChanged -> {
                _state.value = _state.value.copy(
                    verificationCode = event.code,
                    error = null
                )
            }
            is RegisterEvent.RequestVerificationCode -> {
                requestVerificationCode()
            }
            is RegisterEvent.VerifyPhoneNumber -> {
                verifyPhoneNumber()
            }
            is RegisterEvent.NextClicked -> {
                validatePhoneNumberAndProceed()
            }
            is RegisterEvent.RegisterClicked -> {
                register()
            }
        }
    }

    private fun checkUserIdAvailability() {
        if (_state.value.username.isBlank()) {
            _state.value = _state.value.copy(error = "아이디를 입력해주세요.")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(
                isCheckingUserId = true,
                error = null
            )

            userRepository.checkUsername(_state.value.username)
                .onSuccess { isAvailable ->
                    _state.value = _state.value.copy(
                        isCheckingUserId = false,
                        isUserIdAvailable = isAvailable,
                        error = if (!isAvailable) "이미 사용 중인 아이디입니다." else null
                    )
                }
                .onFailure { exception ->
                    _state.value = _state.value.copy(
                        isCheckingUserId = false,
                        error = "아이디 중복 확인 중 오류가 발생했습니다."
                    )
                }
        }
    }

    private fun validatePhoneNumberAndProceed() {
        if (!validatePhoneNumber()) {
            return
        }

        if (!_state.value.isVerificationCodeSent) {
            _state.value = _state.value.copy(error = "휴대전화 인증이 필요합니다.")
            return
        }

        if (!_state.value.isPhoneVerified) {
            _state.value = _state.value.copy(error = "휴대전화 인증을 완료해주세요.")
            return
        }
    }

    private fun requestVerificationCode() {
        if (!validatePhoneNumber()) {
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            // 실제 구현에서는 여기에 API 호출 로직이 들어갈 것입니다
            delay(1000) // 임시 딜레이

            _state.value = _state.value.copy(
                isLoading = false,
                isVerificationCodeSent = true,
                remainingTimeSeconds = 180,
                verificationAttempts = 0,
                verificationCode = "",
                error = null
            )

            startVerificationTimer()
        }
    }

    private fun verifyPhoneNumber() {
        if (!validateVerificationCode()) {
            return
        }

        if (_state.value.verificationAttempts >= _state.value.maxVerificationAttempts) {
            _state.value = _state.value.copy(
                error = "인증 시도 횟수를 초과했습니다. 다시 시도해주세요.",
                isVerificationCodeSent = false
            )
            verificationTimer?.cancel()
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            // 임시로 성공 케이스로 변경
            delay(1000) // 임시 딜레이
            val isVerificationSuccessful = true // 실제 구현 시 API 응답으로 대체

            if (isVerificationSuccessful) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    isPhoneVerified = true,
                    error = null
                )
                verificationTimer?.cancel()
            } else {
                val newAttempts = _state.value.verificationAttempts + 1
                val remainingAttempts = _state.value.maxVerificationAttempts - newAttempts
                _state.value = _state.value.copy(
                    isLoading = false,
                    verificationAttempts = newAttempts,
                    error = "인증번호가 일치하지 않습니다. (남은 시도 횟수: ${remainingAttempts}회)",
                    verificationCode = ""
                )
            }
        }
    }

    private fun startVerificationTimer() {
        verificationTimer?.cancel()
        verificationTimer = viewModelScope.launch {
            while (_state.value.remainingTimeSeconds > 0) {
                delay(1000)
                _state.value = _state.value.copy(
                    remainingTimeSeconds = _state.value.remainingTimeSeconds - 1
                )
            }

            if (!_state.value.isPhoneVerified) {
                _state.value = _state.value.copy(
                    isVerificationCodeSent = false,
                    verificationCode = "",
                    error = "인증 시간이 만료되었습니다."
                )
            }
        }
    }

    private fun validatePhoneNumber(): Boolean {
        if (_state.value.phoneNumber.length != 11) {
            _state.value = _state.value.copy(error = "올바른 휴대전화 번호를 입력해주세요.")
            return false
        }
        return true
    }

    private fun validateVerificationCode(): Boolean {
        if (_state.value.verificationCode.length != 6) {
            _state.value = _state.value.copy(error = "인증번호 6자리를 입력해주세요.")
            return false
        }
        return true
    }

    private fun register() {
        if (!validateInput()) {
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val request = RegisterRequest(
                username = _state.value.username,
                password = _state.value.password,
                nickname = _state.value.nickname,
                phoneNumber = _state.value.phoneNumber,
                verificationCode = _state.value.verificationCode
            )

            userRepository.register(request)
                .onSuccess {
                    userRepository.login(
                        LoginRequest(
                            username = _state.value.username,
                            password = _state.value.password
                        )
                    ).onSuccess {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = null
                        )
                    }.onFailure { loginException ->
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = loginException.message
                        )
                    }
                }
                .onFailure { registerException ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = registerException.message
                    )
                }
        }
    }

    private fun validateInput(): Boolean {
        if (_state.value.username.isBlank()) {
            _state.value = _state.value.copy(error = "아이디를 입력해주세요.")
            return false
        }

        if (!_state.value.isUserIdAvailable) {
            _state.value = _state.value.copy(error = "아이디 중복확인이 필요합니다.")
            return false
        }

        if (_state.value.nickname.isBlank()) {  // 추가
            _state.value = _state.value.copy(error = "닉네임을 입력해주세요.")
            return false
        }

        if (_state.value.password.isBlank()) {
            _state.value = _state.value.copy(error = "비밀번호를 입력해주세요.")
            return false
        }

        if (_state.value.password != _state.value.confirmPassword) {
            _state.value = _state.value.copy(error = "비밀번호가 일치하지 않습니다.")
            return false
        }

        if (!_state.value.isPhoneVerified) {
            _state.value = _state.value.copy(error = "휴대전화 인증이 필요합니다.")
            return false
        }

        return true
    }

    override fun onCleared() {
        super.onCleared()
        verificationTimer?.cancel()
    }
}