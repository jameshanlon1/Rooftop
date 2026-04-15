package com.rooftop.data.local.mapper

import com.rooftop.data.local.entity.PlaylistEntity
import com.rooftop.domain.model.Playlist
import com.rooftop.domain.model.PlaylistType

fun PlaylistEntity.toDomain(): Playlist = Playlist(
    id = id,
    name = name,
    type = runCatching { PlaylistType.valueOf(type) }.getOrDefault(PlaylistType.M3U),
    url = url,
    xtreamBaseUrl = xtreamBaseUrl,
    xtreamUsername = xtreamUsername,
    xtreamPassword = xtreamPassword
)

fun Playlist.toEntity(): PlaylistEntity = PlaylistEntity(
    id = id,
    name = name,
    type = type.name,
    url = url,
    xtreamBaseUrl = xtreamBaseUrl,
    xtreamUsername = xtreamUsername,
    xtreamPassword = xtreamPassword
)
