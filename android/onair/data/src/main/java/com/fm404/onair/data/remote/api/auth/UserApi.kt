package com.fm404.onair.data.remote.api.auth

import com.fm404.onair.core.network.annotation.PublicApi
import com.fm404.onair.data.remote.dto.auth.LoginRequestDto
import com.fm404.onair.data.remote.dto.auth.PhoneVerificationRequestDto
import com.fm404.onair.data.remote.dto.auth.PhoneVerifyRequestDto
import com.fm404.onair.data.remote.dto.auth.PhoneVerifyResponseDto
import com.fm404.onair.data.remote.dto.auth.SignupRequestDto
import com.fm404.onair.data.remote.dto.auth.UpdateNicknameRequestDto
import com.fm404.onair.data.remote.dto.auth.UserInfoDto
import com.fm404.onair.data.remote.dto.auth.UserRoleDto
import com.fm404.onair.data.remote.dto.auth.ValidUsernameResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface UserApi {
    @GET("api/user/role")
    suspend fun checkAdminRole(): UserRoleDto

    @PublicApi
    @GET("api/v1/user/valid-username/{username}")
    suspend fun checkUsername(@Path("username") username: String): ValidUsernameResponse

    @PublicApi
    @POST("api/v1/user/signup")
    suspend fun register(@Body request: SignupRequestDto)

    @PublicApi
    @FormUrlEncoded
    @POST("api/v1/user/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<Unit>

    @PublicApi
    @POST("api/v1/user/phone-verification/verification-code")
    suspend fun requestVerificationCode(@Body request: PhoneVerificationRequestDto)

    @PublicApi
    @POST("api/v1/user/phone-verification")
    suspend fun verifyPhoneNumber(@Body request: PhoneVerifyRequestDto): PhoneVerifyResponseDto

    @GET("api/v1/user/logout")
    suspend fun logout(): Response<Unit>

    @GET("api/v1/user")
    suspend fun getUserInfo(): UserInfoDto

    @PATCH("api/v1/user/nickname")
    suspend fun updateNickname(@Body request: UpdateNicknameRequestDto)

    @Multipart
    @PATCH("api/v1/user/profile-image")
    suspend fun updateProfileImage(@Part file: MultipartBody.Part): Response<Unit>

    @POST("api/v1/user/reissue")
    suspend fun reissue(): Response<Unit>
}