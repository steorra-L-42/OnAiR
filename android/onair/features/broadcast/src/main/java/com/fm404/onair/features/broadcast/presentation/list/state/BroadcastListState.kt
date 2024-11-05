package com.fm404.onair.features.broadcast.presentation.list.state

import com.fm404.onair.domain.model.broadcast.Broadcast

data class BroadcastListState(
    val isLoading: Boolean = false,
    val broadcasts: List<Broadcast> = emptyList(),
    val error: String? = null
    // 추후 방송 목록 데이터 추가
)