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
import android.media.audiofx.Visualizer
import android.util.Log
import javax.inject.Inject
import kotlin.math.absoluteValue

private const val TAG = "BroadcastDetailViewMode"
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
    private var visualizer: Visualizer? = null
    private val _amplitudes = MutableStateFlow(FloatArray(10))
    val amplitudes: StateFlow<FloatArray> = _amplitudes

    init {
        fetchContentTypeHeaders()
        // channelId로 채널 정보 로드
//        Log.d(TAG, "channelID: 채널 아이디: ${_state.value.broadcastId}")
//        Log.d(TAG, "channelID: 채널 제목 : ${_state.value.title}")
        _state.value.broadcastId.takeIf { it.isNotEmpty() }?.let { channelId ->
            Log.d(TAG, "채널 로딩: channelId: $channelId")
            loadChannelDetail(channelId)
        }
    }

    private fun loadChannelDetail(channelId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            getChannelUseCase(channelId)
                .onSuccess { channel ->
                    Log.d(TAG, "loadChannelDetail: 여기여기여기여기 ${channel.channelName}")
                    _state.update { currentState ->
                        currentState.copy(
                            title = channel.channelName,
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
                    Log.e(TAG, "loadChannelDetail: 실패 - ${throwable.message}", throwable) // Add throwable stack trace

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
        viewModelScope.launch {
            try {
                var lastHeaderTime = System.currentTimeMillis()

                customHttpDataSourceFactory.getHeaderStateFlow()
                    .onEach { headers ->
                        // 새로운 헤더를 받을 때마다 시간 업데이트
                        lastHeaderTime = System.currentTimeMillis()

                        val contentType = headers["onair-content-type"] ?: "story"
                        val title = headers["music-title"]
                        val artist = headers["music-artist"]
                        val coverUrl = headers["music-cover"]

                        _state.update { currentState ->
                            currentState.copy(
                                contentType = when (contentType) {
                                    "news" -> "뉴스"
                                    "story" -> "사연"
                                    "weather" -> "날씨"
                                    "music" -> "음악"
                                    else -> "사연"
                                },
                                coverImageUrl = coverUrl,
                                musicTitle = title,
                                musicArtist = artist
                            )
                        }
                    }
                    .transformLatest { headers ->
                        // 세그먼트 체크 로직
                        while(true) {
                            emit(headers)
                            kotlinx.coroutines.delay(5000) // 5초마다 체크

                            val currentTime = System.currentTimeMillis()
                            if (currentTime - lastHeaderTime > 11000) { // 11초 동안 새로운 세그먼트가 없으면
                                _state.update { it.copy(
                                    playerError = true,
                                    error = "방송이 종료되었습니다"
                                )}
                                stopStreaming()
                                break
                            }
                        }
                    }
                    .catch { e ->
                        // 스트림 에러 발생 시
                        _state.update { it.copy(
                            playerError = true,
                            error = "방송이 종료되었습니다"
                        )}
                        stopStreaming()
                    }
                    .collect()
            } catch (e: Exception) {
                _state.update { it.copy(
                    playerError = true,
                    error = "방송 연결에 실패했습니다"
                )}
                stopStreaming()
            }
        }
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
        player = ExoPlayer.Builder(getApplication()).build().apply {
//            val mediaItem = MediaItem.fromUri("http://wonyoung.on-air.me:8000/channel/channel_1/index.m3u8")
//            val mediaItem = MediaItem.fromUri("https://nuguri.on-air.me/channel/channel_1/index.m3u8")
            val mediaItem = MediaItem.fromUri("https://nuguri.on-air.me/channel/${state.value.broadcastId}/index.m3u8")
            val mediaSource = HlsMediaSource.Factory(customHttpDataSourceFactory)
                .createMediaSource(mediaItem)
            setMediaSource(mediaSource)
            prepare()
            play()
        }
        setupVisualizer()

//        val loadControl = DefaultLoadControl.Builder()
//            .setBufferDurationsMs(
//                10 * C.DEFAULT_BUFFER_SEGMENT_SIZE,
//                15 * C.DEFAULT_BUFFER_SEGMENT_SIZE,
//                1000,  // Minimum buffer before playback starts or resumes
//                5000   // Minimum buffer for stable playback without interruptions
//            )
//            .build()

//        val loadControl = DefaultLoadControl.Builder()
//            .setBufferDurationsMs(
//                20000,
//                50000,
//                1000,
//                3000
//            )
//            .build()
//
//        player = ExoPlayer.Builder(getApplication())
//            .setLoadControl(loadControl)  // Set custom load control here
//            .build()
//            .apply {
//                val mediaItem = MediaItem.fromUri("https://nuguri.on-air.me/channel/channel_1/index.m3u8")
//                val mediaSource = HlsMediaSource.Factory(customHttpDataSourceFactory)
//                    .createMediaSource(mediaItem)
//                setMediaSource(mediaSource)
//                prepare()
//                play()
//            }
    }

    private fun setupVisualizer() {
        player?.audioSessionId?.let { sessionId ->
            visualizer = Visualizer(sessionId).apply {
                captureSize = Visualizer.getCaptureSizeRange()[1]
                val samplingRateRatio = 0.5
//                Log.d(TAG, "setupVisualizer: Capture Rate: ${Visualizer.getMaxCaptureRate()}")
                setDataCaptureListener(object : Visualizer.OnDataCaptureListener {
                    override fun onWaveFormDataCapture(
                        visualizer: Visualizer,
                        waveform: ByteArray,
                        samplingRate: Int
                    ) {}

                    override fun onFftDataCapture(
                        visualizer: Visualizer,
                        fft: ByteArray,
                        samplingRate: Int
                    ) {
                        updateAmplitudes(fft)
                    }
                }, (Visualizer.getMaxCaptureRate() * samplingRateRatio).toInt(), false, true)
                enabled = true
            }
        }
    }

    private fun updateAmplitudes(fft: ByteArray) {
        val amplitudes = FloatArray(10)
        for (i in amplitudes.indices) {
            amplitudes[i] = fft.getOrNull(i)?.toFloat()?.absoluteValue ?: 0f
        }
        _amplitudes.value = amplitudes
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
