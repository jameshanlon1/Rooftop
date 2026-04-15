package com.rooftop.domain.model

data class Playlist(
    val id: Long = 0L,
    val name: String,
    val type: PlaylistType,
    val url: String? = null,
    val xtreamBaseUrl: String? = null,
    val xtreamUsername: String? = null,
    val xtreamPassword: String? = null
)
