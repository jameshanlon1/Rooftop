package com.rooftop.domain.model

data class Series(
    val id: Long,
    val name: String,
    val coverUrl: String?,
    val category: String?,
    val rating: String?,
    val playlistId: Long = 0L
)
