package com.fm404.onair.features.broadcast.presentation.detail

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.fm404.onair.core.contract.media.MediaPlayerContract
import com.fm404.onair.features.broadcast.presentation.detail.state.BroadcastDetailState
import com.fm404.onair.features.broadcast.presentation.detail.state.BroadcastDetailEvent
import kotlinx.coroutines.flow.*

@HiltViewModel
class BroadcastDetailViewModel @Inject constructor(
    private val mediaPlayerContract: MediaPlayerContract,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(
        BroadcastDetailState(
        broadcastId = savedStateHandle.get<String>("broadcastId") ?: ""
    )
    )
    val state = _state.asStateFlow()

    fun onEvent(event: BroadcastDetailEvent) {
        when (event) {
            is BroadcastDetailEvent.ToggleStreaming -> {
                if (_state.value.isPlaying) {
                    mediaPlayerContract.stopMediaStream()
                    _state.update { it.copy(isPlaying = false) }
                } else {
                    mediaPlayerContract.startMediaStream(_state.value.broadcastId)  // broadcastId를 channelName으로 사용
                    _state.update { it.copy(isPlaying = true) }
                }
            }
        }
    }
}