package com.fm404.onair.domain.repository.auth

import com.fm404.onair.domain.model.auth.UserRole

interface UserRepository {
    suspend fun checkAdminRole(): Result<UserRole>
}
