package com.fm404.onair.features.broadcast.presentation.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.fm404.onair.core.contract.media.MediaPlayerContract
import com.fm404.onair.features.broadcast.presentation.detail.state.BroadcastDetailState
import com.fm404.onair.features.broadcast.presentation.detail.state.BroadcastDetailEvent
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class BroadcastDetailViewModel @Inject constructor(
    private val mediaPlayerContract: MediaPlayerContract,
    savedStateHandle: SavedStateHandle,
    application: Application
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(
        BroadcastDetailState(
            broadcastId = savedStateHandle.get<String>("broadcastId") ?: ""
        )
    )
    val state = _state.asStateFlow()

    private var player: ExoPlayer? = null

    fun onEvent(event: BroadcastDetailEvent) {
        when (event) {
            is BroadcastDetailEvent.ToggleStreaming -> {
                if (_state.value.isPlaying) {
                    stopStreaming()
                } else {
                    startStreaming()
                }
            }
        }
    }

    private fun startStreaming() {
        mediaPlayerContract.startMediaStream(_state.value.broadcastId)
        initializePlayer()
        _state.update { it.copy(isPlaying = true) }
    }

    private fun stopStreaming() {
        mediaPlayerContract.stopMediaStream()
        releasePlayer()
        _state.update { it.copy(isPlaying = false) }
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(getApplication()).build().apply {
            val mediaItem = MediaItem.fromUri("http://wonyoung.on-air.me:8000/channel/channel_1/index.m3u8")
            setMediaItem(mediaItem)
            prepare()
            play()
        }
    }

    private fun releasePlayer() {
        player?.release()
        player = null
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }
}
