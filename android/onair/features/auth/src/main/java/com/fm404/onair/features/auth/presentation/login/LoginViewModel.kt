package com.fm404.onair.features.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.fm404.onair.core.contract.auth.AuthNavigationContract
import com.fm404.onair.core.contract.auth.NavControllerHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.fm404.onair.features.auth.presentation.login.state.LoginState
import com.fm404.onair.features.auth.presentation.login.state.LoginEvent

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val navigationContract: AuthNavigationContract
) : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun setNavController(navController: NavHostController) {
        // contract를 통해 간접적으로 navController 설정
        (navigationContract as? NavControllerHolder)?.setNavController(navController)
    }

    fun clearNavController() {
        (navigationContract as? NavControllerHolder)?.setNavController(null)
    }

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.UsernameChanged -> {
                _state.value = _state.value.copy(
                    username = event.username,
                    error = null
                )
            }
            is LoginEvent.PasswordChanged -> {
                _state.value = _state.value.copy(
                    password = event.password,
                    error = null
                )
            }
            is LoginEvent.LoginClicked -> {
                login()
            }
            is LoginEvent.RegisterClicked -> {
                navigationContract.navigateToRegister()
            }
        }
    }

    private fun login() {
        if (!validateInput()) {
            return
        }

        _state.value = _state.value.copy(isLoading = true)

        _state.value = _state.value.copy(
            isLoading = false,
            error = null
        )
    }

    private fun validateInput(): Boolean {
        if (_state.value.username.isBlank()) {
            _state.value = _state.value.copy(error = "아이디를 입력해주세요.")
            return false
        }

        if (_state.value.password.isBlank()) {
            _state.value = _state.value.copy(error = "비밀번호를 입력해주세요.")
            return false
        }

        return true
    }
}