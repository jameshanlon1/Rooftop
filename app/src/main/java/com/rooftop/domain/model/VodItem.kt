package com.rooftop.domain.model

data class VodItem(
    val id: Long,
    val name: String,
    val streamUrl: String,
    val posterUrl: String?,
    val category: String?,
    val rating: String?,
    val year: String?,
    val playlistId: Long = 0L
)
