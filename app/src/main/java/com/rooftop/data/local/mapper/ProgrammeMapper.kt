package com.rooftop.data.local.mapper

import com.rooftop.data.local.entity.ProgrammeEntity
import com.rooftop.domain.model.Programme

fun ProgrammeEntity.toDomain() = Programme(
    id = id,
    channelId = channelId,
    title = title,
    start = startMs,
    stop = stopMs,
    description = description,
    category = category,
    iconUrl = iconUrl,
    hasReminder = hasReminder
)

fun Programme.toEntity() = ProgrammeEntity(
    id = id,
    channelId = channelId,
    title = title,
    startMs = start,
    stopMs = stop,
    description = description,
    category = category,
    iconUrl = iconUrl,
    hasReminder = hasReminder
)
