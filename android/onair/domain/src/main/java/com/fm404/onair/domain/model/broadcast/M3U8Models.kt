package com.fm404.onair.domain.model.broadcast

data class M3U8Playlist(
    val segments: List<M3U8Segment>,
    val targetDuration: Int,
    val mediaSequence: Int
)

data class M3U8Segment(
    val duration: Float,
    val uri: String
)