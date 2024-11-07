package com.fm404.onair.features.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.fm404.onair.core.contract.auth.AuthNavigationContract
import com.fm404.onair.core.contract.auth.NavControllerHolder
import com.fm404.onair.domain.usecase.auth.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.fm404.onair.features.auth.presentation.login.state.LoginState
import com.fm404.onair.features.auth.presentation.login.state.LoginEvent
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
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
        val currentState = _state.value

        viewModelScope.launch {
            _state.value = currentState.copy(isLoading = true)

            loginUseCase(
                username = currentState.username,
                password = currentState.password
            ).onSuccess {
                _state.value = currentState.copy(
                    isLoading = false,
                    error = null
                )
                navigationContract.navigateToHome()
            }.onFailure { exception ->
                _state.value = currentState.copy(
                    isLoading = false,
                    error = exception.message ?: "로그인에 실패했습니다."
                )
            }
        }
    }
}