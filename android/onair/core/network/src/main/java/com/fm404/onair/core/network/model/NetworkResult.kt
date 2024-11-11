package com.fm404.onair.core.network.model

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T): NetworkResult<T>()
    data class Error(
        val code: String,
        val message: String,
        val httpCode: Int
    ): NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()
}
