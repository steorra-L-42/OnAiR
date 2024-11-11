package com.fm404.onair.domain.repository.auth

import com.fm404.onair.domain.model.auth.LoginRequest
import com.fm404.onair.domain.model.auth.LoginResult
import com.fm404.onair.domain.model.auth.RegisterRequest
import com.fm404.onair.domain.model.auth.UserRole

interface UserRepository {
    suspend fun checkAdminRole(): Result<UserRole>
    suspend fun checkUsername(username: String): Result<Boolean>
    suspend fun register(request: RegisterRequest): Result<Unit>
    suspend fun login(request: LoginRequest): Result<LoginResult>
    suspend fun requestVerificationCode(phoneNumber: String): Result<Unit>
    suspend fun verifyPhoneNumber(phoneNumber: String, verificationCode: String): Result<Boolean>
    suspend fun logout(): Result<Unit>
}
