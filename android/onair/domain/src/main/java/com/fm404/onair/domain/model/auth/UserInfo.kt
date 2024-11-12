package com.fm404.onair.domain.model.auth

data class UserInfo(
    val userId: Long,
    val nickname: String,
    val username: String,
    val phoneNumber: String,
    val profilePath: String?,
    val role: String
)