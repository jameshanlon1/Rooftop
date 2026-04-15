package com.rooftop.data.local.mapper

import com.rooftop.data.local.entity.VodEntity
import com.rooftop.domain.model.VodItem

fun VodEntity.toDomain(): VodItem = VodItem(
    id = id,
    name = name,
    streamUrl = streamUrl,
    posterUrl = posterUrl,
    category = category,
    rating = rating,
    year = year,
    playlistId = playlistId
)

fun VodItem.toEntity(playlistId: Long): VodEntity = VodEntity(
    id = id,
    name = name,
    streamUrl = streamUrl,
    posterUrl = posterUrl,
    category = category,
    rating = rating,
    year = year,
    playlistId = playlistId
)
