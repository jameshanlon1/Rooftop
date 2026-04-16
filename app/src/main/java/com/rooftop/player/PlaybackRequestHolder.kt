package com.rooftop.player

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaybackRequestHolder @Inject constructor() {
    var request: PlaybackRequest? = null
}

data class PlaybackRequest(
    val contentId: String,
    val streamUrl: String,
    val title: String,
    val posterUrl: String?,
    val savedPositionMs: Long = 0L,
    val contentType: String = "VOD"   // "VOD" or "EPISODE"
)
