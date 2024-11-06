package com.fm404.onair.data.remote.dto.broadcast

import com.google.gson.annotations.SerializedName

data class CreateBroadcastRequest(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String
)