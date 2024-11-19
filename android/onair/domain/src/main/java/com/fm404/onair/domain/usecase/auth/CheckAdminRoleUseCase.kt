package com.fm404.onair.domain.usecase.auth

import com.fm404.onair.domain.model.auth.UserRole
import com.fm404.onair.domain.repository.auth.UserRepository

class CheckAdminRoleUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<UserRole> {
        return userRepository.checkAdminRole()
    }
}