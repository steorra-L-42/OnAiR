package com.fm404.onair.data.repository.auth

import com.fm404.onair.core.network.manager.TokenManager
import com.fm404.onair.data.remote.api.auth.UserApi
import com.fm404.onair.data.remote.dto.auth.LoginRequestDto
import com.fm404.onair.data.remote.dto.auth.SignupRequestDto
import com.fm404.onair.domain.model.auth.LoginRequest
import com.fm404.onair.domain.model.auth.LoginResult
import com.fm404.onair.domain.model.auth.RegisterRequest
import com.fm404.onair.domain.model.auth.UserRole
import com.fm404.onair.domain.repository.auth.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val tokenManager: TokenManager
) : UserRepository {
    override suspend fun checkAdminRole(): Result<UserRole> = runCatching {
        UserRole(userApi.checkAdminRole().isAdmin)
    }

    override suspend fun checkUsername(username: String): Result<Boolean> = runCatching {
//        userApi.checkUsername(username).result
        true
    }

    override suspend fun register(request: RegisterRequest): Result<LoginResult> = runCatching {
        userApi.register(
            SignupRequestDto(
                username = request.username,
                password = request.password,
                nickname = request.nickname,
                phoneNumber = request.phoneNumber,
                verification = request.verificationCode
            )
        )

        // 회원가입 성공 시 바로 로그인
        login(LoginRequest(
            username = request.username,
            password = request.password
        )).getOrThrow()
    }

    override suspend fun login(request: LoginRequest): Result<LoginResult> = runCatching {
        val response = userApi.login(
            LoginRequestDto(
                username = request.username,
                password = request.password
            )
        )

        if (response.isSuccessful) {
            // access token은 Authorization 헤더에서 가져와서 저장
            response.headers()["Authorization"]?.let { token ->
                tokenManager.saveToken(token)
            }

            // refresh token은 쿠키로 자동 저장되므로 별도 처리 불필요
            LoginResult(isSuccess = true)
        } else {
            throw Exception("Login failed: ${response.code()}")
        }
    }
}