package com.fm404.onair.core.network.interceptor

import com.fm404.onair.core.network.annotation.PublicApi
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation
import javax.inject.Inject

class AuthInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val isPublicApi = request.tag(Invocation::class.java)
            ?.method()
            ?.annotations
            ?.any { it is PublicApi }
            ?: false

        val requestBuilder = request.newBuilder().apply {
            // 기본 헤더 추가
            addHeader("Content-Type", "application/json")
            addHeader("Accept", "application/json")

            // Public API가 아닌 경우에만 토큰 추가
            if (!isPublicApi) {
                // 토큰 관리 로직이 구현되기 전까지는 예외를 발생시키지 않음
                // TODO: TokenManager 구현 후 아래 로직으로 변경
                /*
                getToken()?.let { token ->
                    addHeader("Authorization", "Bearer $token")
                } ?: throw UnauthorizedException("Authentication token is required but not found")
                */
            }
        }

        return chain.proceed(requestBuilder.build())
    }

    private fun getToken(): String? {
        // TokenManager나 DataStore에서 토큰 가져오기
        // TODO: 실제 구현 필요
        return null
    }
}

class UnauthorizedException(message: String) : Exception(message)