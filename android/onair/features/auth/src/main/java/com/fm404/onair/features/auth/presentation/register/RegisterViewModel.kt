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
import android.content.Context
import android.telephony.TelephonyManager
import android.util.Log

private const val TAG = "RegisterViewModel"

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
                    usernameError = if (!isUsernameValid(event.username))
                        "4~20자의 영문, 숫자 허용 및 영문으로 시작"
                    else
                        null,
                    isUserIdAvailable = false
                )
            }
            is RegisterEvent.PasswordChanged -> {
                val newPassword = event.password
                _state.value = _state.value.copy(
                    password = newPassword,
                    passwordError = if (!isPasswordValid(newPassword))
                        "8~40자 영문, 숫자, 특수문자(!@#%^&*()-_=+[]{}|;:,<.>?)"
                    else
                        null,
                    // 비밀번호 바뀌면 비밀번호 확인과 일치하는지 비교하는 코드
                    confirmPasswordError = if (_state.value.confirmPassword.isNotEmpty()
                        && _state.value.confirmPassword != newPassword)
                        "비밀번호가 일치하지 않습니다."
                    else
                        null
                )
            }
            is RegisterEvent.ConfirmPasswordChanged -> {
                _state.value = _state.value.copy(
                    confirmPassword = event.confirmPassword,
                    confirmPasswordError = if (event.confirmPassword != _state.value.password)
                        "비밀번호 불일치"
                    else
                        null
                )
            }
            is RegisterEvent.NicknameChanged -> {
                _state.value = _state.value.copy(
                    nickname = event.nickname,
                    nicknameError = if (!isNicknameValid(event.nickname))
                        "2~24자 한글(초성불가), 영문, 숫자"
                    else
                        null
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

    // 아이디: 4~20 길이, 소문자만 허용, 알파벳으로 시작
    private fun isUsernameValid(username: String): Boolean {
        val usernameRegex = "^[a-z][a-z0-9]{3,19}$".toRegex()
        return username.matches(usernameRegex)
    }

    // 비번: 최소 8자 + 영문, 숫자 필수, 특수문자는 ( !@#%^&*()-_=+[]{}|;:,<.>? ) 만 허용
    private fun isPasswordValid(password: String): Boolean {
        val passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#%^&*()\\-_=+\\[\\]{}|;:,<.>?]{8,40}$".toRegex()
        return password.matches(passwordRegex)
    }

    // 닉네임: 2~24 글자, 영숫한
    private fun isNicknameValid(nickname: String): Boolean {
        val nicknameRegex = "^[가-힣a-zA-Z0-9]{2,24}$".toRegex()
        // 글자 세기. 한글은 2글자로 침.
        val length = nickname.fold(0) { acc, char ->
            acc + if (char in '가'..'힣') 2 else 1
        }
        return length in 2..24 && nickname.matches(nicknameRegex)
    }

    private fun getCleanPhoneNumber(phoneNumber: String): String{
        return phoneNumber.replace("[^0-9]".toRegex(), "")
    }

    private fun isValidPhoneNumber(retrievedPhoneNumber: String): Boolean {
        val phoneNumber = getCleanPhoneNumber(retrievedPhoneNumber)
        return phoneNumber.startsWith("010") || phoneNumber.startsWith("8210")
    }

    fun retrievePhoneNumber(context: Context) {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
        try {
            val phoneNumber = telephonyManager?.line1Number
            if (!phoneNumber.isNullOrBlank()) {
                if (isValidPhoneNumber(phoneNumber)) {
                    val formattedNumber = formatPhoneNumber(phoneNumber)
                    updatePhoneNumberState(formattedNumber)
                } else {
                    handleInvalidPhoneNumberFormat(phoneNumber)
                }
            } else {
                _state.value = _state.value.copy(
                    phoneError = "휴대전화 번호를 확인할 수 없습니다."
                )
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "권한이 거부되어 전화번호를 가져올 수 없습니다.", e)
            _state.value = _state.value.copy(
                phoneError = "전화번호 접근 권한이 필요합니다."
            )
        }
    }

    private fun formatPhoneNumber(phoneNumber: String): PhoneNumberFormat {
        // 모든 특수문자 제거 및 숫자만 추출
        val cleaned = getCleanPhoneNumber(phoneNumber)

        // +82로 시작하는 경우 처리
        val nationalNumber = if (cleaned.startsWith("82")) {
            "0${cleaned.substring(2)}"
        } else {
            cleaned
        }

        // 전화번호 길이 검증 (11자리)
        return if (nationalNumber.length == 11 && nationalNumber.startsWith("010")) {
            val displayFormat = nationalNumber.replaceFirst("(\\d{3})(\\d{4})(\\d{4})".toRegex(), "$1-$2-$3")
            PhoneNumberFormat(displayFormat = displayFormat, backendFormat = nationalNumber)
        } else {
            throw IllegalPhoneNumberFormatException("유효하지 않은 전화번호 형식입니다: $phoneNumber")
        }
    }

    private fun updatePhoneNumberState(formattedNumber: PhoneNumberFormat) {
        _state.value = _state.value.copy(
            phoneNumberForUI = formattedNumber.displayFormat,
            phoneNumber = formattedNumber.backendFormat,
            phoneError = null
        )
    }

    private fun handleInvalidPhoneNumberFormat(phoneNumber: String) {
        Log.e(TAG, "handleInvalidPhoneNumberFormat: 유효하지 않은 전화번호 형식, $phoneNumber")
        _state.value = _state.value.copy(
            phoneError = "올바른 휴대전화 번호 형식이 아닙니다."
        )
    }

    class IllegalPhoneNumberFormatException(message: String) : IllegalArgumentException(message)

    data class PhoneNumberFormat(
        val displayFormat: String,  // e.g., "0aa-bbbb-cccc" UI용
        val backendFormat: String   // e.g., "0aabbbbcccc" 백엔드 전송용
    )

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

            userRepository.requestVerificationCode(_state.value.phoneNumber)
                .onSuccess {
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
                .onFailure { exception ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = exception.message ?: "인증번호 요청 중 오류가 발생했습니다."
                    )
                }
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

            userRepository.verifyPhoneNumber(
                _state.value.phoneNumber,
                _state.value.verificationCode
            ).onSuccess { isVerified ->
                if (isVerified) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isPhoneVerified = true,
                        error = null
                    )
                    verificationTimer?.cancel()
                } else {
                    handleVerificationFailure()
                }
            }.onFailure { exception ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = exception.message ?: "인증 확인 중 오류가 발생했습니다."
                )
            }
        }
    }

    private fun handleVerificationFailure() {
        val newAttempts = _state.value.verificationAttempts + 1
        val remainingAttempts = _state.value.maxVerificationAttempts - newAttempts
        _state.value = _state.value.copy(
            isLoading = false,
            verificationAttempts = newAttempts,
            error = "인증번호가 일치하지 않습니다. (남은 시도 횟수: ${remainingAttempts}회)",
            verificationCode = ""
        )
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