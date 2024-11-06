package com.fm404.onair.features.broadcast.presentation.create.state

data class BroadcastCreateState(
    val title: String = "",
    val description: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)