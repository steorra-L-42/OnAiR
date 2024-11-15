package com.fm404.onair.features.broadcast.impl

import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import kotlinx.coroutines.flow.MutableStateFlow

class CustomHttpDataSourceFactory(
    private val headerStateFlow: MutableStateFlow<Map<String, String>>
) : HttpDataSource.Factory {
    private var defaultRequestProperties: Map<String, String> = emptyMap()

//    override fun createDataSource(): HttpDataSource {
//        val dataSource = CustomHttpDataSource()
//        defaultRequestProperties.forEach { (key, value) ->
//            dataSource.setRequestProperty(key, value)
//        }
//        return dataSource
//    }

    override fun createDataSource(): HttpDataSource {
        val dataSource = CustomHttpDataSource(headerStateFlow)
        defaultRequestProperties.forEach { (key, value) ->
            dataSource.setRequestProperty(key, value)
        }
        return dataSource
    }

    override fun setDefaultRequestProperties(defaultRequestProperties: Map<String, String>): HttpDataSource.Factory {
        this.defaultRequestProperties = defaultRequestProperties
        return this
    }

    fun getContentTypeHeaders(): Map<String, String> {
        // Placeholder for accessing and returning headers like [onair-content-type], [music-title], etc.
        return mapOf(
            "onair-content-type" to "story",   // Mock data; in real case, retrieve from the HTTP request
            "music-title" to "Some Song",
            "music-artist" to "Some Artist",
            "music-cover" to "https://some.image.url/imageurl.extension"
        )
    }

    // Public getter for headerStateFlow
    fun getHeaderStateFlow(): MutableStateFlow<Map<String, String>> = headerStateFlow
}
