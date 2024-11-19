package com.fm404.onair.features.auth.presentation.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.fm404.onair.core.contract.auth.AuthNavigationContract
import com.fm404.onair.core.contract.auth.FCMServiceContract
import com.fm404.onair.core.contract.auth.NavControllerHolder
import com.fm404.onair.domain.usecase.auth.LoginUseCase
import com.fm404.onair.domain.usecase.auth.RegisterFCMTokenUseCase
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
    private val registerFCMTokenUseCase: RegisterFCMTokenUseCase,
    private val navigationContract: AuthNavigationContract,
    private val fcmServiceContract: FCMServiceContract
) : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun setNavController(navController: NavHostController) {
        Log.d("LoginViewModel", "Setting NavController")
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
                Log.d("LoginViewModel", "Login success, registering FCM token")
                // 로그인 성공 시 FCM 토큰 등록
                fcmServiceContract.getToken { token ->
                    viewModelScope.launch {
                        registerFCMTokenUseCase(token)
                            .onSuccess {
                                Log.d("LoginViewModel", "FCM token registered, attempting navigation")
                                _state.value = currentState.copy(
                                    isLoading = false,
                                    error = null
                                )
                                navigationContract.navigateToBroadcastList()
                            }
                            .onFailure { exception ->
                                Log.e("LoginViewModel", "FCM token registration failed", exception)
                                _state.value = currentState.copy(
                                    isLoading = false,
                                    error = exception.message ?: "FCM 토큰 등록에 실패했습니다."
                                )
                            }
                    }
                }
            }.onFailure { exception ->
                Log.e("LoginViewModel", "Login failed", exception)
                _state.value = currentState.copy(
                    isLoading = false,
                    error = exception.message ?: "로그인에 실패했습니다."
                )
            }
        }
    }
}