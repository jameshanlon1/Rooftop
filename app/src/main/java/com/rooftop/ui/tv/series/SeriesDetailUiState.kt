package com.rooftop.ui.tv.series

import com.rooftop.domain.model.Episode
import com.rooftop.domain.model.SeriesDetail

data class SeriesDetailUiState(
    val detail: SeriesDetail? = null,
    val selectedSeason: Int = 1,
    val selectedEpisode: Episode? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)
