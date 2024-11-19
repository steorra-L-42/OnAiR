package com.fm404.onair.data.repository.auth

import com.fm404.onair.core.network.exception.CustomException
import com.fm404.onair.core.network.manager.TokenManager
import com.fm404.onair.core.network.model.ErrorResponse
import com.fm404.onair.data.mapper.toDomain
import com.fm404.onair.data.remote.api.auth.UserApi
import com.fm404.onair.data.remote.dto.auth.*
import com.fm404.onair.domain.model.auth.*
import com.fm404.onair.domain.repository.auth.UserRepository
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.create
import java.io.File
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val tokenManager: TokenManager
) : UserRepository {
    override suspend fun checkAdminRole(): Result<UserRole> = runCatching {
        UserRole(userApi.checkAdminRole().isAdmin)
    }

    override suspend fun checkUsername(username: String): Result<Boolean> = runCatching {
        userApi.checkUsername(username).result
    }

    override suspend fun register(request: RegisterRequest): Result<Unit> = runCatching {
        userApi.register(
            SignupRequestDto(
                username = request.username,
                password = request.password,
                nickname = request.nickname,
                phoneNumber = request.phoneNumber,
                verification = request.verificationCode
            )
        )
    }

    override suspend fun login(request: LoginRequest): Result<LoginResult> = runCatching {
        val response = userApi.login(
            username = request.username,
            password = request.password
        )

        if (response.isSuccessful) {
            // access token 저장
            response.headers()["Authorization"]?.let { token ->
                tokenManager.saveAccessToken(token)
            }

            LoginResult(isSuccess = true)
        } else {
            val errorBody = response.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            throw CustomException(
                code = errorResponse.code,
                message = errorResponse.message,
                httpCode = response.code()
            ).toDomainException()
        }
    }

    override suspend fun requestVerificationCode(phoneNumber: String): Result<Unit> = runCatching {
        userApi.requestVerificationCode(PhoneVerificationRequestDto(phoneNumber = phoneNumber))
    }

    override suspend fun verifyPhoneNumber(
        phoneNumber: String,
        verificationCode: String
    ): Result<Boolean> = runCatching {
        userApi.verifyPhoneNumber(
            PhoneVerifyRequestDto(
                phoneNumber = phoneNumber,
                verification = verificationCode
            )
        ).result
    }

    override suspend fun logout(): Result<Unit> = runCatching {
        try {
            // 1. 먼저 로컬 토큰 삭제
            tokenManager.clearTokens()

            // 2. 서버 로그아웃 시도
            val response = userApi.logout()
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)

                // 토큰 관련 에러가 아닌 경우에만 예외를 던짐
                if (!errorResponse.code.startsWith("C")) {
                    throw CustomException(
                        code = errorResponse.code,
                        message = errorResponse.message,
                        httpCode = response.code()
                    ).toDomainException()
                }
            }
        } catch (e: Exception) {
            // 네트워크 오류 등이 발생해도 로컬 토큰은 이미 삭제된 상태
            throw e
        }
    }

    override suspend fun getUserInfo(): Result<UserInfo> = runCatching {
        userApi.getUserInfo().toDomain()
    }

    override suspend fun updateNickname(nickname: String): Result<Unit> = runCatching {
        userApi.updateNickname(UpdateNicknameRequestDto(nickname = nickname))
    }

    override suspend fun updateProfileImage(imageFile: File): Result<UpdateProfileResponse> = runCatching {
        val requestBody = create(
            "image/*".toMediaTypeOrNull(),
            imageFile
        )
        val part = MultipartBody.Part.createFormData("upload", imageFile.name, requestBody)
        userApi.updateProfileImage(part).toDomain()
    }

    override suspend fun registerFCMToken(request: FCMTokenRequest): Result<Unit> = runCatching {
        val response = userApi.registerToken(request.fcmToken)

        if (response.isSuccessful) {
            Unit
        } else {
            val errorBody = response.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            throw CustomException(
                code = errorResponse.code,
                message = errorResponse.message,
                httpCode = response.code()
            ).toDomainException()
        }
    }
}