package com.fm404.onair.domain.usecase.auth

import com.fm404.onair.domain.model.auth.LoginRequest
import com.fm404.onair.domain.model.auth.LoginResult
import com.fm404.onair.domain.repository.auth.UserRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(username: String, password: String): Result<LoginResult> {
        if (username.isBlank()) {
            return Result.failure(IllegalArgumentException("아이디를 입력해주세요."))
        }

        if (password.isBlank()) {
            return Result.failure(IllegalArgumentException("비밀번호를 입력해주세요."))
        }

        return repository.login(
            LoginRequest(
                username = username,
                password = password
            )
        )
    }
}