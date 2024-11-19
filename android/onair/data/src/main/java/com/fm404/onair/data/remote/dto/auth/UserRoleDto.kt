package com.fm404.onair.data.remote.dto.auth

import com.google.gson.annotations.SerializedName

data class UserRoleDto(
    @SerializedName("is_admin")
    val isAdmin: Boolean
)