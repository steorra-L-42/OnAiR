package com.fm404.onair.service

import android.content.Intent
import android.os.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import android.app.*
import android.media.AudioAttributes
import android.media.MediaPlayer
import com.fm404.onair.core.contract.media.MediaServiceBinder
import com.fm404.onair.core.contract.media.MediaServiceContract
import com.fm404.onair.domain.usecase.media.GetMediaStreamUseCase
import kotlinx.coroutines.*

@AndroidEntryPoint
class MediaPlayerService : Service(), MediaServiceContract {
    @Inject
    lateinit var getMediaStreamUseCase: GetMediaStreamUseCase

    private val binder = MediaPlayerBinderImpl()
    private var mediaPlayer: MediaPlayer? = null

    inner class MediaPlayerBinderImpl : Binder(), MediaServiceBinder {
        override fun getService(): MediaServiceContract = this@MediaPlayerService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
        }
    }

    override fun startStream(channelName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            getMediaStreamUseCase(channelName).fold(
                onSuccess = { mediaStream ->
                    withContext(Dispatchers.Main) {
                        mediaPlayer?.apply {
                            reset()
                            setDataSource(mediaStream.url)
                            prepareAsync()
                            setOnPreparedListener {
                                start()
                            }
                        }
                    }
                },
                onFailure = {
                    // Handle error
                }
            )
        }
    }

    override fun stopStream() {
        mediaPlayer?.stop()
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }
}