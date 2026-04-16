package com.rooftop.data.local.mapper

import com.rooftop.data.local.entity.WatchProgressEntity
import com.rooftop.domain.model.ContentType
import com.rooftop.domain.model.WatchProgress

fun WatchProgressEntity.toDomain() = WatchProgress(
    contentId = contentId,
    title = title,
    posterUrl = posterUrl,
    streamUrl = streamUrl,
    positionMs = positionMs,
    durationMs = durationMs,
    contentType = runCatching { ContentType.valueOf(contentType) }.getOrDefault(ContentType.VOD),
    updatedAt = updatedAt
)

fun WatchProgress.toEntity() = WatchProgressEntity(
    contentId = contentId,
    title = title,
    posterUrl = posterUrl,
    streamUrl = streamUrl,
    positionMs = positionMs,
    durationMs = durationMs,
    contentType = contentType.name,
    updatedAt = updatedAt
)
