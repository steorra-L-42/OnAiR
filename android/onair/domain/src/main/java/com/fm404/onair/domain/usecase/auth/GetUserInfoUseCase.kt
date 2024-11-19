package com.fm404.onair.domain.usecase.auth

import com.fm404.onair.domain.model.auth.UserInfo
import com.fm404.onair.domain.repository.auth.UserRepository
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<UserInfo> =
        userRepository.getUserInfo()
}