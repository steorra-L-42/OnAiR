package com.fm404.onair.features.broadcast.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fm404.onair.features.broadcast.presentation.create.state.BroadcastCreateEvent
import com.fm404.onair.features.broadcast.presentation.create.state.BroadcastCreateState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BroadcastCreateViewModel @Inject constructor(
    // 필요한 UseCase들 주입
) : ViewModel() {

    private val _state = MutableStateFlow(BroadcastCreateState())
    val state = _state.asStateFlow()

    fun onEvent(event: BroadcastCreateEvent) {
        when (event) {
            is BroadcastCreateEvent.OnTitleChange -> {
                _state.update { it.copy(title = event.title) }
            }
            is BroadcastCreateEvent.OnDescriptionChange -> {
                _state.update { it.copy(description = event.description) }
            }
            BroadcastCreateEvent.OnCreateClick -> {
                createBroadcast()
            }
        }
    }

    private fun createBroadcast() {
        // 방송 생성 로직 구현
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // TODO: 방송 생성 UseCase 호출

            _state.update { it.copy(isLoading = false) }
        }
    }
}