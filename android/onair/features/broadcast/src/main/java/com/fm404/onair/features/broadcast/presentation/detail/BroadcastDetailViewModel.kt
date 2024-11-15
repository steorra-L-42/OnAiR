package com.fm404.onair.features.broadcast.presentation.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.fm404.onair.core.contract.media.MediaPlayerContract
import com.fm404.onair.domain.usecase.broadcast.broadcast.GetChannelUseCase
import com.fm404.onair.features.broadcast.impl.CustomHttpDataSourceFactory
import com.fm404.onair.features.broadcast.presentation.detail.state.BroadcastDetailState
import com.fm404.onair.features.broadcast.presentation.detail.state.BroadcastDetailEvent
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BroadcastDetailViewModel @Inject constructor(
    private val mediaPlayerContract: MediaPlayerContract,
    private val customHttpDataSourceFactory: CustomHttpDataSourceFactory,
    private val getChannelUseCase: GetChannelUseCase,
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

    init {
        fetchContentTypeHeaders()
        // channelId로 채널 정보 로드
        _state.value.broadcastId.takeIf { it.isNotEmpty() }?.let { channelId ->
            loadChannelDetail(channelId)
        }
    }

    private fun loadChannelDetail(channelId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            getChannelUseCase(channelId)
                .onSuccess { channel ->
                    _state.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            error = null,
                            userNickname = channel.userNickname,
                            profilePath = channel.profilePath,
                            ttsEngine = channel.ttsEngine,
                            personality = channel.personality,
                            topic = channel.newsTopic,
                            isDefault = channel.isDefault,
                            start = channel.start,
                            end = channel.end,
                            isEnded = channel.isEnded,
                            thumbnail = channel.thumbnail,
                            coverImageUrl = channel.thumbnail
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

    private fun fetchContentTypeHeaders() {
//        viewModelScope.launch {
//            val headers = customHttpDataSourceFactory.getContentTypeHeaders()
//            val contentType = headers["onair-content-type"] ?: "story"
//            val title = headers["music-title"]
//            val artist = headers["music-artist"]
//            val coverUrl = headers["music-cover"]
//
//            _state.update { currentState ->
//                currentState.copy(
//                    contentType = when (contentType) {
//                        "news" -> "뉴스"
//                        "story" -> "사연"
//                        "weather" -> "날씨"
//                        "music" -> {
//                            if (!title.isNullOrEmpty() && !artist.isNullOrEmpty()) {
//                                "$artist - $title"
//                            } else "음악"
//                        }
//
//                        else -> "사연"
//                    },
//                    coverImageUrl = coverUrl
//                )
//            }
//        }
        customHttpDataSourceFactory.getHeaderStateFlow()
            .onEach { headers ->
                val contentType = headers["onair-content-type"] ?: "story"
                val title = headers["music-title"]
                val artist = headers["music-artist"]
                val coverUrl = headers["music-cover"]

                // Update state with dynamic content info
                _state.update { currentState ->
                    currentState.copy(
                        contentType = when (contentType) {
                            "news" -> "뉴스"
                            "story" -> "사연"
                            "weather" -> "날씨"
                            "music" -> {
                                if (!title.isNullOrEmpty() && !artist.isNullOrEmpty()) {
                                    "$artist - $title"
                                } else "음악"
                            }
                            else -> "사연"
                        },
                        coverImageUrl = coverUrl
                    )
                }
            }
            .launchIn(viewModelScope)
    }

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
//        player = ExoPlayer.Builder(getApplication()).build().apply {
////            val mediaItem = MediaItem.fromUri("http://wonyoung.on-air.me:8000/channel/channel_1/index.m3u8")
//            val mediaItem = MediaItem.fromUri("https://nuguri.on-air.me/channel/channel_1/index.m3u8")
//            val mediaSource = HlsMediaSource.Factory(customHttpDataSourceFactory)
//                .createMediaSource(mediaItem)
//            setMediaSource(mediaSource)
//            prepare()
//            play()
//        }

        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                10 * C.DEFAULT_BUFFER_SEGMENT_SIZE,
                15 * C.DEFAULT_BUFFER_SEGMENT_SIZE,
                1000,  // Minimum buffer before playback starts or resumes
                5000   // Minimum buffer for stable playback without interruptions
            )
            .build()

        player = ExoPlayer.Builder(getApplication())
            .setLoadControl(loadControl)  // Set custom load control here
            .build()
            .apply {
                val mediaItem = MediaItem.fromUri("https://nuguri.on-air.me/channel/channel_1/index.m3u8")
                val mediaSource = HlsMediaSource.Factory(customHttpDataSourceFactory)
                    .createMediaSource(mediaItem)
                setMediaSource(mediaSource)
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
