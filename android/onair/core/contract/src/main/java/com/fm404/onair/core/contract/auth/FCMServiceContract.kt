package com.fm404.onair.core.contract.auth

interface FCMServiceContract {
    fun getToken(callback: (String) -> Unit)
}