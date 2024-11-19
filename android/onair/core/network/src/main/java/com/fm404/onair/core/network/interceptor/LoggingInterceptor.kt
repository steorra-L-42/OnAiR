package com.fm404.onair.core.network.interceptor

import android.util.Log
import com.fm404.onair.core.network.BuildConfig
import okhttp3.*
import javax.inject.Inject

class LoggingInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (BuildConfig.DEBUG) {
            Log.d("API", "-->")
            Log.d("API", "URL: ${request.url}")
            Log.d("API", "Method: ${request.method}")
            request.headers.forEach { (name, value) ->
                Log.d("API", "Header: $name: $value")
            }
            request.body?.let {
                val buffer = okio.Buffer()
                it.writeTo(buffer)
                Log.d("API", "Body: ${buffer.readUtf8()}")
            }
        }

        val response = chain.proceed(request)

        if (BuildConfig.DEBUG) {
            Log.d("API", "<--")
            Log.d("API", "Code: ${response.code}")
            response.headers.forEach { (name, value) ->
                Log.d("API", "Header: $name: $value")
            }
            response.body?.let {
                val bodyString = it.string() // 주의: 응답 body를 소비합니다!
                Log.d("API", "Response Body: $bodyString")

                // body를 다시 만들어서 반환
                return response.newBuilder()
                    .body(ResponseBody.create(it.contentType(), bodyString))
                    .build()
            }
        }

        return response
    }
}