package com.fm404.onair.features.auth.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fm404.onair.core.contract.auth.AuthNavigationContract
import com.fm404.onair.domain.usecase.auth.CheckAdminRoleUseCase
import com.fm404.onair.features.auth.presentation.admin.state.AdminEvent
import com.fm404.onair.features.auth.presentation.admin.state.AdminState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val checkAdminRoleUseCase: CheckAdminRoleUseCase,
    private val authNavigationContract: AuthNavigationContract
) : ViewModel() {
    private val _state = MutableStateFlow(AdminState())
    val state = _state.asStateFlow()

    fun onEvent(event: AdminEvent) {
        when (event) {
            is AdminEvent.OnBackClick -> authNavigationContract.navigateBack()
        }
    }
}