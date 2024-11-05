package com.fm404.onair.core.common.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel<S, E> : ViewModel() {
    private val _uiState = MutableStateFlow(createInitialState())
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    abstract fun createInitialState(): S

    abstract fun onEvent(event: E)

    protected fun setState(reduce: S.() -> S) {
        val newState = uiState.value.reduce()
        _uiState.value = newState
    }
}