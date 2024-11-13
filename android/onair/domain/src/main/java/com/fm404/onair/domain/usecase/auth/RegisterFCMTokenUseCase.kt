package com.fm404.onair.domain.usecase.auth

import com.fm404.onair.domain.model.auth.FCMTokenRequest
import com.fm404.onair.domain.repository.auth.UserRepository
import javax.inject.Inject

class RegisterFCMTokenUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(token: String): Result<Unit> {
        return userRepository.registerFCMToken(FCMTokenRequest(fcmToken = token))
    }
}