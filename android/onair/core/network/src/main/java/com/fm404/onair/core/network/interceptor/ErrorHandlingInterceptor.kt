package com.fm404.onair.core.network.interceptor

import com.fm404.onair.core.network.exception.CustomException
import com.fm404.onair.core.network.model.ErrorResponse
import com.fm404.onair.domain.exception.DomainException
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
            val errorBody = response.body?.string()

            // 에러 바디가 있을 경우 파싱 시도
            if (!errorBody.isNullOrEmpty()) {
                try {
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    if (errorResponse != null && errorResponse.code.isNotEmpty()) {
                        // 새로운 response 객체를 만들어서 반환
                        return response.newBuilder()
                            .body(errorBody.toResponseBody(response.body?.contentType()))
                            .build()
                    }
                } catch (e: Exception) {
                    // 파싱 실패시 기본 에러 응답 생성
                    val defaultError = ErrorResponse(
                        code = getDefaultErrorCode(response.code),
                        message = getDefaultErrorMessage(response.code)
                    )
                    val defaultErrorBody = Gson().toJson(defaultError)
                    return response.newBuilder()
                        .body(defaultErrorBody.toResponseBody("application/json".toMediaType()))
                        .build()
                }
            }

            // 에러 바디가 없는 경우 기본 에러 응답 생성
            val defaultError = ErrorResponse(
                code = getDefaultErrorCode(response.code),
                message = getDefaultErrorMessage(response.code)
            )
            val defaultErrorBody = Gson().toJson(defaultError)
            return response.newBuilder()
                .body(defaultErrorBody.toResponseBody("application/json".toMediaType()))
                .build()
        }

        return response
    }

    private fun getDefaultErrorCode(httpCode: Int): String {
        return when (httpCode) {
            400 -> "A001"
            401 -> "C001"
            403 -> "A003"
            404 -> "B004"
            409 -> "B001"
            500 -> "D001"
            else -> "UNKNOWN"
        }
    }

    private fun getDefaultErrorMessage(httpCode: Int): String {
        return when (httpCode) {
            400 -> "잘못된 요청입니다"
            401 -> "로그인이 필요합니다"
            403 -> "권한이 없습니다"
            404 -> "존재하지 않는 유저입니다"
            409 -> "이미 존재하는 계정입니다"
            500 -> "서버 오류가 발생했습니다"
            else -> "알 수 없는 오류가 발생했습니다"
        }
    }
}