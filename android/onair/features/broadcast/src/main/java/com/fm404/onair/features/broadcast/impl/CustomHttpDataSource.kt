package com.fm404.onair.features.broadcast.impl

import android.util.Log
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import kotlinx.coroutines.flow.MutableStateFlow

private const val TAG = "CustomHttpDataSource"
class CustomHttpDataSource(
    private val headerStateFlow: MutableStateFlow<Map<String, String>>
) : DefaultHttpDataSource() {
//    override fun open(dataSpec: DataSpec): Long {
//        val result = super.open(dataSpec)
//        val headers = responseHeaders // Access response headers
//        headers.forEach { (key, value) ->
//            Log.d("$TAG ts 파일 Header 까보기 - ", "$key: $value")
//        }
//        return result
//    }

    override fun open(dataSpec: DataSpec): Long {
        val result = super.open(dataSpec)

        // Convert responseHeaders from Map<String, List<String>> to Map<String, String>
        val simplifiedHeaders = responseHeaders.mapValues { (_, value) -> value.firstOrNull() ?: "" }

        // Log each header for debugging
        simplifiedHeaders.forEach { (key, value) ->
            Log.d("$TAG TS Header", "$key: $value")
        }

        // Update state with the latest headers
        headerStateFlow.value = simplifiedHeaders
        return result
    }
}
