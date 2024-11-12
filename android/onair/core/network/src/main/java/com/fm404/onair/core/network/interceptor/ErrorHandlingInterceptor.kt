package com.fm404.onair.core.network.interceptor

import com.fm404.onair.core.network.model.ErrorResponse
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import javax.inject.Inject

class ErrorHandlingInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (!response.isSuccessful) {
            val contentType = "application/json".toMediaType()

            // Response Body를 안전하게 읽기
            val bodyString = try {
                response.peekBody(Long.MAX_VALUE).string()
            } catch (e: Exception) {
                ""
            }

            if (bodyString.isNotEmpty()) {
                try {
                    val errorResponse = Gson().fromJson(bodyString, ErrorResponse::class.java)
                    if (errorResponse != null && errorResponse.code.isNotEmpty()) {
                        // 서버 응답이 있는 경우 그대로 사용
                        return response.newBuilder()
                            .body(bodyString.toResponseBody(contentType))
                            .build()
                    }
                } catch (e: Exception) {
                    // 파싱 실패 시 기본 에러 응답 생성
                    val defaultErrorBody = Gson().toJson(
                        ErrorResponse(code = "999", message = "알 수 없는 오류가 발생했습니다")
                    )
                    return response.newBuilder()
                        .body(defaultErrorBody.toResponseBody(contentType))
                        .build()
                }
            }

            // 에러 바디가 없는 경우 기본 에러 응답 생성
            val defaultErrorBody = Gson().toJson(
                ErrorResponse(code = "888", message = "알 수 없는 오류가 발생했습니다")
            )
            return response.newBuilder()
                .body(defaultErrorBody.toResponseBody(contentType))
                .build()
        }

        return response
    }
}