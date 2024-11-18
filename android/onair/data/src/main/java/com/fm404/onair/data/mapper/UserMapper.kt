package com.fm404.onair.data.mapper

import com.fm404.onair.data.remote.dto.auth.UpdateProfileResponseDto
import com.fm404.onair.data.remote.dto.auth.UserInfoDto
import com.fm404.onair.domain.model.auth.UpdateProfileResponse
import com.fm404.onair.domain.model.auth.UserInfo

fun UserInfoDto.toDomain() = UserInfo(
    userId = userId,
    nickname = nickname,
    username = username,
    phoneNumber = phoneNumber,
    profilePath = profilePath,
    role = role
)

fun UpdateProfileResponseDto.toDomain() = UpdateProfileResponse(
    nickname = nickname,
    url = url
)