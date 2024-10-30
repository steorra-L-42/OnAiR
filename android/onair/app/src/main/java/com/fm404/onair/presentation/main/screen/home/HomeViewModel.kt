package com.fm404.onair.presentation.main.screen.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.fm404.onair.presentation.main.screen.home.state.HomeState
import com.fm404.onair.presentation.main.screen.home.state.HomeEvent

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.OnNavigate -> {
                _state.value = _state.value.copy(currentRoute = event.route)
            }
        }
    }
}