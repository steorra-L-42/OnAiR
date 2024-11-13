package com.fm404.onair.core.network.interceptor

import com.fm404.onair.core.network.annotation.PublicApi
import com.fm404.onair.core.network.manager.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val isPublicApi = request.tag(Invocation::class.java)
            ?.method()
            ?.annotations
            ?.any { it is PublicApi }
            ?: false

        // 새로운 request builder 생성
        val newRequest = request.newBuilder().apply {
            // 기본 헤더 추가
            addHeader("Content-Type", "application/json")
            addHeader("Accept", "application/json")

            // Public API가 아닌 경우에만 토큰 추가
            if (!isPublicApi) {
                runBlocking {
                    tokenManager.getAccessTokenBlocking()?.let { token ->
                        addHeader("Authorization", token)
                    }
                }
            }
        }.build()

        return chain.proceed(newRequest)
    }
}