package com.fm404.onair.domain.exception

class DomainException(
    val code: String,
    override val message: String
) : Exception(message)