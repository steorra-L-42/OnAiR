package com.fm404.onair.core.contract.media

interface MediaServiceContract {
    fun startStream(channelName: String)
    fun stopStream()
}