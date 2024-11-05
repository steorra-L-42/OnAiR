package com.fm404.onair.features.broadcast.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fm404.onair.domain.usecase.broadcast.broadcast.GetBroadcastListUseCase
import com.fm404.onair.features.broadcast.presentation.list.state.BroadcastListEvent
import com.fm404.onair.features.broadcast.presentation.list.state.BroadcastListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BroadcastListViewModel @Inject constructor(
    private val getBroadcastListUseCase: GetBroadcastListUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(BroadcastListState())
    val state = _state.asStateFlow()

    init {
        loadBroadcasts()
    }

    fun onEvent(event: BroadcastListEvent) {
        when (event) {
            is BroadcastListEvent.LoadBroadcasts -> loadBroadcasts()
            is BroadcastListEvent.OnBroadcastClick -> {
                // 필요한 경우 방송 클릭 처리
            }
        }
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
}