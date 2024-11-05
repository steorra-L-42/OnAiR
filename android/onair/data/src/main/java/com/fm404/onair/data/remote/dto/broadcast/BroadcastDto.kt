package com.fm404.onair.data.remote.dto.broadcast

import com.google.gson.annotations.SerializedName

data class BroadcastDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("start_time") val startTime: Long,
    @SerializedName("is_live") val isLive: Boolean
)