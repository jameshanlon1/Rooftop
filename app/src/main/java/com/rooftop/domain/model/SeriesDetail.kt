package com.rooftop.domain.model

data class SeriesDetail(
    val series: Series,
    val plot: String?,
    val cast: String?,
    val genre: String?,
    val backdropUrl: String?,
    val seasons: Map<Int, List<Episode>>  // season number -> episodes
)
