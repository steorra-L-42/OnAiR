package com.fm404.onair.data.remote.dto.auth

import com.google.gson.annotations.SerializedName

data class UserInfoDto(
    @SerializedName("user_id") val userId: Long,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("username") val username: String,
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("profilePath") val profilePath: String?,
    @SerializedName("role") val role: String
)