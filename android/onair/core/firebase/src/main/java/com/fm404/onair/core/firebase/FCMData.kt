package com.fm404.onair.core.firebase

import java.io.Serializable

data class FCMData(
    val channelId: String?,
    val channelName: String?,
    val storyTitle: String?,
    val type: String?

) : Serializable
