package com.fm404.onair.data.remote.dto.auth

import com.google.gson.annotations.SerializedName

data class UpdateNicknameRequestDto(
    @SerializedName("nickname") val nickname: String
)