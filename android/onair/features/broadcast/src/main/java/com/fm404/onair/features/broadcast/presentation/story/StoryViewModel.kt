package com.fm404.onair.features.broadcast.presentation.story

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.fm404.onair.core.common.base.BaseViewModel
import com.fm404.onair.domain.model.story.CreateStoryRequest
import com.fm404.onair.domain.usecase.broadcast.story.CreateStoryUseCase
import com.fm404.onair.features.broadcast.presentation.story.state.StoryEvent
import com.fm404.onair.features.broadcast.presentation.story.state.StoryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class StoryViewModel @Inject constructor(
    private val createStoryUseCase: CreateStoryUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<StoryState, StoryEvent>() {

    private val broadcastId: String = checkNotNull(savedStateHandle["broadcastId"])

    override fun createInitialState(): StoryState = StoryState()

    override fun onEvent(event: StoryEvent) {
        when (event) {
            is StoryEvent.OnTitleChange -> {
                setState { copy(title = event.title) }
            }
            is StoryEvent.OnContentChange -> {
                setState { copy(content = event.content) }
            }
            is StoryEvent.OnMusicSelect -> {
                setState { copy(selectedMusic = event.music) }
            }
            StoryEvent.OnSubmit -> {
                createStory()
            }
        }
    }

    private fun createStory() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }

            val request = CreateStoryRequest(
                title = uiState.value.title,
                content = uiState.value.content,
                music = uiState.value.selectedMusic
            )

            createStoryUseCase(broadcastId, request)
                .onSuccess {
                    // 성공 시 이전 화면으로 이동
                    _navigationEvent.emit(Unit)
                }
                .onFailure { throwable ->
                    setState { copy(error = throwable.message) }
                }

            setState { copy(isLoading = false) }
        }
    }
}