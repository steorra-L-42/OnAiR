package com.fm404.onair.core.contract.media

interface MediaPlayerContract {
    fun startMediaStream(channelName: String)
    fun stopMediaStream()
}