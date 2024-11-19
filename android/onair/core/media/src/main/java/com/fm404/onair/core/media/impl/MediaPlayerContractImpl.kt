package com.fm404.onair.core.media.impl

import android.content.*
import android.os.IBinder
import com.fm404.onair.core.contract.media.MediaPlayerContract
import com.fm404.onair.core.contract.media.MediaServiceBinder
import com.fm404.onair.core.contract.media.MediaServiceContract
import javax.inject.Inject

class MediaPlayerContractImpl @Inject constructor(
    private val context: Context
) : MediaPlayerContract {
    private var mediaService: MediaServiceContract? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MediaServiceBinder
            mediaService = binder.getService()
            pendingChannelName?.let {
                mediaService?.startStream(it)
                pendingChannelName = null
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mediaService = null
        }
    }
    private var pendingChannelName: String? = null

    override fun startMediaStream(channelName: String) {
        val intent = Intent(context, Class.forName("com.fm404.onair.service.MediaPlayerService"))

        if (mediaService == null) {
            pendingChannelName = channelName
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            context.startService(intent)
        } else {
            mediaService?.startStream(channelName)
        }
    }

    override fun stopMediaStream() {
        mediaService?.stopStream()
    }
}