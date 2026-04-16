package com.rooftop.domain.model

data class VodInfo(
    val item: VodItem,
    val plot: String?,
    val cast: String?,
    val director: String?,
    val genre: String?,
    val backdropUrl: String?
)
