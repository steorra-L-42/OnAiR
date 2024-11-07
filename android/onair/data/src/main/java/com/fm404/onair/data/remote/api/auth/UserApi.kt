package com.fm404.onair.data.remote.api.auth

import com.fm404.onair.core.network.annotation.PublicApi
import com.fm404.onair.data.remote.dto.auth.LoginRequestDto
import com.fm404.onair.data.remote.dto.auth.SignupRequestDto
import com.fm404.onair.data.remote.dto.auth.UserRoleDto
import com.fm404.onair.data.remote.dto.auth.ValidUsernameResponse
import retrofit2.Response
import retrofit2.http.*

interface UserApi {
    @GET("api/user/role")
    suspend fun checkAdminRole(): UserRoleDto

    @PublicApi
    @GET("api/v1/user/valid-username")
    suspend fun checkUsername(@Query("username") username: String): ValidUsernameResponse

    @PublicApi
    @POST("api/v1/user/signup")
    suspend fun register(@Body request: SignupRequestDto)

    @PublicApi
    @POST("api/v1/user/login")
    suspend fun login(@Body request: LoginRequestDto): Response<Unit>
}