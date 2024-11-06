package com.fm404.onair.domain.model.broadcast

data class Broadcast(
    val id: String,
    val title: String,
    val description: String,
    val startTime: Long,
    val isLive: Boolean
)