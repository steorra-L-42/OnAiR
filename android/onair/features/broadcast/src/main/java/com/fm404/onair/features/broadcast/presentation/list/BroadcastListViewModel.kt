package com.fm404.onair.features.broadcast.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fm404.onair.core.contract.auth.AuthNavigationContract
import com.fm404.onair.core.contract.broadcast.BroadcastNavigationContract
import com.fm404.onair.domain.usecase.auth.GetUserInfoUseCase
import com.fm404.onair.domain.usecase.broadcast.broadcast.GetBroadcastListUseCase
import com.fm404.onair.domain.usecase.broadcast.broadcast.GetChannelListUseCase
import com.fm404.onair.features.broadcast.presentation.list.state.BroadcastListEvent
import com.fm404.onair.features.broadcast.presentation.list.state.BroadcastListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BroadcastListViewModel @Inject constructor(
    private val getBroadcastListUseCase: GetBroadcastListUseCase,
    private val getChannelListUseCase: GetChannelListUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val broadcastNavigationContract: BroadcastNavigationContract,
    private val authNavigationContract: AuthNavigationContract
) : ViewModel() {
    private val _state = MutableStateFlow(BroadcastListState())
    val state = _state.asStateFlow()

    init {
        validateUserAndLoadChannels()
    }

    fun onEvent(event: BroadcastListEvent) {
        when (event) {
            is BroadcastListEvent.LoadBroadcasts -> loadBroadcasts()
            is BroadcastListEvent.LoadChannels -> validateUserAndLoadChannels()
            is BroadcastListEvent.OnBroadcastClick -> {
                broadcastNavigationContract.navigateToBroadcastDetail(event.broadcastId)
            }
            is BroadcastListEvent.OnChannelClick -> {
                broadcastNavigationContract.navigateToBroadcastDetail(event.channelUuid)
            }
            is BroadcastListEvent.OnNotificationClick -> {
                broadcastNavigationContract.navigateToNotification()
            }
        }
    }

    private fun validateUserAndLoadChannels() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            getUserInfoUseCase()
                .onSuccess { userInfo ->
                    loadChannels()
                }
                .onFailure { throwable ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message
                        )
                    }
                    authNavigationContract.navigateToLogin()
                }
        }
    }

    fun onNotificationClick() {
        onEvent(BroadcastListEvent.OnNotificationClick)
    }

    private fun loadBroadcasts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            getBroadcastListUseCase()
                .onSuccess { broadcasts ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            broadcasts = broadcasts,
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

    fun retryLoad() {
        validateUserAndLoadChannels()
    }
}