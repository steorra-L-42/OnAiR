package com.fm404.onair.data.remote.dto.broadcast

data class CreateChannelRequest(
    val ttsEngine: String,
    val personality: String,
    val topic: String,
    val playList: List<PlayListDto>
)

data class PlayListDto(
    val playListMusicTitle: String,
    val playListMusicArtist: String,
    val playListMusicCoverUrl: String
)

data class CreateChannelResponse(
    val userId: String,
    val role: String,
    val tts_engine: String,
    val personality: String,
    val topic: String,
    val playList: List<PlayListDto>,
    val channelId: String,
    val isDefault: Boolean,
    val start: String,
    val end: String,
    val isEnded: Boolean,
    val thumbnail: String
)