package com.fm404.onair.data.repository.auth

import com.fm404.onair.data.remote.api.auth.UserApi
import com.fm404.onair.domain.model.auth.UserRole
import com.fm404.onair.domain.repository.auth.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi
) : UserRepository {
    override suspend fun checkAdminRole(): Result<UserRole> = runCatching {
        val response = userApi.checkAdminRole()
        UserRole(isAdmin = response.isAdmin)
    }
}