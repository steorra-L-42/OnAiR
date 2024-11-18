package com.fm404.onair.features.broadcast.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fm404.onair.core.common.util.BroadcastConstants
import com.fm404.onair.core.contract.broadcast.BroadcastNavigationContract
import com.fm404.onair.domain.usecase.broadcast.broadcast.CreateChannelUseCase
import com.fm404.onair.features.broadcast.presentation.create.state.BroadcastCreateEvent
import com.fm404.onair.features.broadcast.presentation.create.state.BroadcastCreateState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BroadcastCreateViewModel @Inject constructor(
    private val createChannelUseCase: CreateChannelUseCase,
    private val broadcastNavigationContract: BroadcastNavigationContract
) : ViewModel() {

    private val _state = MutableStateFlow(BroadcastCreateState(
        ttsEngine = "TYPECAST_SENA",
        personality = "GENTLE",
        newsTopic = BroadcastConstants.NEWS_TOPIC_OPTIONS.keys.firstOrNull() ?: "",
        channelName = createChannelName(
            ttsEngine = "TYPECAST_SENA",
            personality = "GENTLE",
            newsTopic = BroadcastConstants.NEWS_TOPIC_OPTIONS.keys.firstOrNull() ?: ""
        )
    ))
    val state = _state.asStateFlow()

    fun onEvent(event: BroadcastCreateEvent) {
        when (event) {
            is BroadcastCreateEvent.OnTtsEngineChange -> {
                _state.update {
                    val updatedState = it.copy(
                        ttsEngine = event.ttsEngine,
                        thumbnail = BroadcastConstants.TTS_THUMBNAIL_MAPPING[event.ttsEngine] ?: ""
                    )
                    updatedState.copy(channelName = createChannelName(
                        ttsEngine = updatedState.ttsEngine,
                        personality = updatedState.personality,
                        newsTopic = updatedState.newsTopic
                    ))
                }
            }
            is BroadcastCreateEvent.OnPersonalityChange -> {
                _state.update {
                    val updatedState = it.copy(personality = event.personality)
                    updatedState.copy(channelName = createChannelName(
                        ttsEngine = updatedState.ttsEngine,
                        personality = updatedState.personality,
                        newsTopic = updatedState.newsTopic
                    ))
                }
            }
            is BroadcastCreateEvent.OnNewsTopicChange -> {
                _state.update {
                    val updatedState = it.copy(newsTopic = event.newsTopic)
                    updatedState.copy(channelName = createChannelName(
                        ttsEngine = updatedState.ttsEngine,
                        personality = updatedState.personality,
                        newsTopic = updatedState.newsTopic
                    ))
                }
            }
            is BroadcastCreateEvent.OnTrackListChange -> {
                _state.update { it.copy(trackList = event.trackList) }
            }
            is BroadcastCreateEvent.OnThumbnailChange -> {
                _state.update { it.copy(thumbnail = event.thumbnail) }
            }
            is BroadcastCreateEvent.OnChannelNameChange -> {
                _state.update { it.copy(channelName = event.channelName) }
            }
            BroadcastCreateEvent.OnCreateClick -> {
                createBroadcast()
            }
        }
    }

    private fun createChannelName(ttsEngine: String, personality: String, newsTopic: String): String {
        val ttsEngineName = BroadcastConstants.TTS_ENGINE_OPTIONS[ttsEngine] ?: ttsEngine
        val personalityName = BroadcastConstants.PERSONALITY_OPTIONS[personality] ?: personality
        val newsTopicName = BroadcastConstants.NEWS_TOPIC_OPTIONS[newsTopic] ?: newsTopic
        return "${ttsEngineName}의 $personalityName $newsTopicName"
    }

    private fun createBroadcast() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val currentState = state.value
            createChannelUseCase(
                ttsEngine = currentState.ttsEngine,
                personality = currentState.personality,
                newsTopic = currentState.newsTopic,
                thumbnail = currentState.thumbnail,
                channelName = currentState.channelName,
                trackList = currentState.trackList
            ).onSuccess { createChannelResult ->
                _state.update { it.copy(isLoading = false) }
                broadcastNavigationContract.navigateToBroadcastDetail(createChannelResult.channelUuid)
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