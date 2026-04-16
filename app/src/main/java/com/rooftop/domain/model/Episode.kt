package com.rooftop.domain.model

data class Episode(
    val id: Long,
    val seriesId: Long,
    val title: String,
    val season: Int,
    val episodeNum: Int,
    val streamUrl: String,
    val duration: String? = null,
    val plot: String? = null
)
