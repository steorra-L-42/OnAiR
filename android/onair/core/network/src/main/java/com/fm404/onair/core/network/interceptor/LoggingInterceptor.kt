package com.fm404.onair.core.network.interceptor

import android.util.Log
import com.fm404.onair.BuildConfig
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
                Log.d("API", "Body: ${it.toString()}")
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
                Log.d("API", "Body: ${it.toString()}")
            }
        }

        return response
    }
}