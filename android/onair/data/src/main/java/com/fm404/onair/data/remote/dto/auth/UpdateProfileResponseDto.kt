package com.fm404.onair.data.remote.dto.auth

import com.google.gson.annotations.SerializedName

data class UpdateProfileResponseDto(
    @SerializedName("nickname") val nickname: String,
    @SerializedName("url") val url: String
)