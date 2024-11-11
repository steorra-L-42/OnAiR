package com.fm404.onair.core.network.exception

import com.fm404.onair.domain.exception.DomainException

class CustomException(
    val code: String,
    override val message: String,
    val httpCode: Int
) : Exception(message) {
    fun toDomainException(): DomainException {
        return DomainException(code, message)
    }
}