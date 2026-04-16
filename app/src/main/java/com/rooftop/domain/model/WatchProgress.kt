package com.rooftop.domain.model

data class WatchProgress(
    val contentId: String,     // "vod_{id}" or "episode_{seriesId}_{season}_{ep}"
    val title: String,
    val posterUrl: String?,
    val streamUrl: String,
    val positionMs: Long,
    val durationMs: Long,
    val contentType: ContentType,
    val updatedAt: Long = System.currentTimeMillis()
) {
    val progressFraction: Float
        get() = if (durationMs > 0) (positionMs.toFloat() / durationMs).coerceIn(0f, 1f) else 0f
}

enum class ContentType { VOD, EPISODE }
