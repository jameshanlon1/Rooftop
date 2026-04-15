package com.rooftop.domain.model

data class Channel(
    val id: Long,
    val name: String,
    val streamUrl: String,
    val logoUrl: String?,
    val groupTitle: String?,
    val epgChannelId: String?,
    val streamType: StreamType = StreamType.LIVE,
    val playlistId: Long = 0L
)
