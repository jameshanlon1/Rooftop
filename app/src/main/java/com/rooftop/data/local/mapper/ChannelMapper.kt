package com.rooftop.data.local.mapper

import com.rooftop.data.local.entity.ChannelEntity
import com.rooftop.domain.model.Channel
import com.rooftop.domain.model.StreamType

fun ChannelEntity.toDomain(): Channel = Channel(
    id = id,
    name = name,
    streamUrl = streamUrl,
    logoUrl = logoUrl,
    groupTitle = groupTitle,
    epgChannelId = epgChannelId,
    streamType = runCatching { StreamType.valueOf(streamType) }.getOrDefault(StreamType.LIVE),
    playlistId = playlistId
)

fun Channel.toEntity(playlistId: Long): ChannelEntity = ChannelEntity(
    id = id,
    name = name,
    streamUrl = streamUrl,
    logoUrl = logoUrl,
    groupTitle = groupTitle,
    epgChannelId = epgChannelId,
    streamType = streamType.name,
    playlistId = playlistId
)
