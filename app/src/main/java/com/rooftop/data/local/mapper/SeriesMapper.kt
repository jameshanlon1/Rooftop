package com.rooftop.data.local.mapper

import com.rooftop.data.local.entity.SeriesEntity
import com.rooftop.domain.model.Series

fun SeriesEntity.toDomain(): Series = Series(
    id = id,
    name = name,
    coverUrl = coverUrl,
    category = category,
    rating = rating,
    playlistId = playlistId
)

fun Series.toEntity(playlistId: Long): SeriesEntity = SeriesEntity(
    id = id,
    name = name,
    coverUrl = coverUrl,
    category = category,
    rating = rating,
    playlistId = playlistId
)
