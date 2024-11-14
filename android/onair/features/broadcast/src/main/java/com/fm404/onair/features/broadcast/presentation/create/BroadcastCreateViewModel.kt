package com.fm404.onair.features.broadcast.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fm404.onair.domain.usecase.broadcast.broadcast.CreateChannelUseCase
import com.fm404.onair.features.broadcast.presentation.create.state.BroadcastCreateEvent
import com.fm404.onair.features.broadcast.presentation.create.state.BroadcastCreateState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BroadcastCreateViewModel @Inject constructor(
    private val createChannelUseCase: CreateChannelUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(BroadcastCreateState(
        ttsEngine = "이상철" // 기본값 설정
    ))
    val state = _state.asStateFlow()

    fun onEvent(event: BroadcastCreateEvent) {
        when (event) {
            is BroadcastCreateEvent.OnTtsEngineChange -> {
                _state.update { it.copy(ttsEngine = event.ttsEngine) }
            }
            is BroadcastCreateEvent.OnPersonalityChange -> {
                _state.update { it.copy(personality = event.personality) }
            }
            is BroadcastCreateEvent.OnTopicChange -> {
                _state.update { it.copy(topic = event.topic) }
            }
            is BroadcastCreateEvent.OnPlayListChange -> {
                _state.update { it.copy(playList = event.playList) }
            }
            BroadcastCreateEvent.OnCreateClick -> {
                createBroadcast()
            }
        }
    }

    private fun createBroadcast() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val currentState = state.value
            createChannelUseCase(
                ttsEngine = currentState.ttsEngine,
                personality = currentState.personality,
                topic = currentState.topic,
                playList = currentState.playList
            ).onSuccess {
                _state.update { it.copy(isLoading = false) }
                // TODO: 성공 처리 (네비게이션)
            }.onFailure { exception ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = exception.message ?: "채널 생성에 실패했습니다."
                    )
                }
            }
        }
    }

    fun onErrorDismiss() {
        _state.update { it.copy(error = null) }
    }
}