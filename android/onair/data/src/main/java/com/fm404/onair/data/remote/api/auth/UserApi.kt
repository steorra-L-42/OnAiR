package com.fm404.onair.data.remote.api.auth

import com.fm404.onair.data.remote.dto.auth.UserRoleDto
import retrofit2.http.GET

interface UserApi {
    @GET("api/user/role")
    suspend fun checkAdminRole(): UserRoleDto
}