package com.fm404.onair.core.network.interceptor

import com.fm404.onair.core.network.exception.CustomException
import com.fm404.onair.core.network.manager.TokenManager
import com.fm404.onair.core.network.model.ErrorResponse
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Named

class TokenReissueInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
    @Named("baseUrl") private val baseUrl: String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response = chain.proceed(request)

        if (response.code == 401) {
            val errorBody = response.body?.string()

            // errorBody가 null이 아닌 경우에만 처리
            if (!errorBody.isNullOrEmpty()) {
                try {
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)

                    // errorResponse가 null이 아닌 경우에만 C001 체크
                    if (errorResponse?.code == "C001") {
                        response = runBlocking {
                            try {
                                // 토큰 재발급 요청
                                val reissueRequest = Request.Builder()
                                    .url("${baseUrl}api/v1/user/reissue")
                                    .post(okhttp3.RequestBody.create(null, ByteArray(0)))
                                    .build()

                                val reissueResponse = chain.proceed(reissueRequest)

                                if (reissueResponse.isSuccessful) {
                                    reissueResponse.headers["Authorization"]?.let { newToken ->
                                        tokenManager.saveAccessToken(newToken)
                                    }

                                    // 기존 응답 닫기
                                    response.close()
                                    reissueResponse.close()

                                    // 새로운 토큰으로 원래 요청 재시도
                                    val newRequest = request.newBuilder()
                                        .header("Authorization", tokenManager.getAccessTokenBlocking() ?: "")
                                        .build()
                                    chain.proceed(newRequest)
                                } else {
                                    val reissueErrorBody = reissueResponse.body?.string()
                                    val reissueErrorResponse = Gson().fromJson(reissueErrorBody, ErrorResponse::class.java)

                                    when (reissueErrorResponse?.code) {
                                        "C002", "C003", "C004", "C005" -> {
                                            tokenManager.clearTokens()
                                            throw CustomException(
                                                code = reissueErrorResponse.code,
                                                message = reissueErrorResponse.message,
                                                httpCode = reissueResponse.code
                                            ).toDomainException()
                                        }
                                    }
                                    reissueResponse.close()
                                    response
                                }
                            } catch (e: Exception) {
                                response
                            }
                        }
                    }
                } catch (e: Exception) {
                    // JSON 파싱 실패 시 기존 응답 반환
                    return response
                }
            }
        }

        return response
    }
}