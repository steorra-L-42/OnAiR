package com.fm404.onair.features.broadcast.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fm404.onair.core.contract.broadcast.BroadcastNavigationContract
import com.fm404.onair.domain.usecase.broadcast.broadcast.GetChannelListUseCase
import com.fm404.onair.features.broadcast.presentation.list.state.BroadcastListEvent
import com.fm404.onair.features.broadcast.presentation.list.state.BroadcastListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BroadcastListViewModel @Inject constructor(
    private val getChannelListUseCase: GetChannelListUseCase,
    private val broadcastNavigationContract: BroadcastNavigationContract
) : ViewModel() {
    private val _state = MutableStateFlow(BroadcastListState())
    val state = _state.asStateFlow()

    init {
        loadChannels()
    }

    fun onEvent(event: BroadcastListEvent) {
        when (event) {
            is BroadcastListEvent.LoadChannels -> loadChannels()
            is BroadcastListEvent.OnChannelClick -> {
                // 필요한 경우 방송 클릭 처리
            }
            is BroadcastListEvent.OnNotificationClick -> {
                broadcastNavigationContract.navigateToNotification()
            }
        }
    }

    fun onNotificationClick() {
        onEvent(BroadcastListEvent.OnNotificationClick)
    }

    private fun loadChannels() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            getChannelListUseCase()
                .onSuccess { channels ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            channels = channels,
                            error = null
                        )
                    }
                }
                .onFailure { throwable ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message
                        )
                    }
                }
        }
    }
}