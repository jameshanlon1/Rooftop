package com.rooftop.ui.tv.home

import com.rooftop.domain.model.Series
import com.rooftop.domain.model.VodItem
import com.rooftop.domain.model.WatchProgress

// §2.1 — up to 8 items rotate in the hero banner
data class FeaturedItem(
    val title: String,
    val backdropUrl: String?,
    val description: String = "",
    val seriesId: Long? = null,
    val vodId: Long? = null
)

data class HomeUiState(
    val featuredItems: List<FeaturedItem> = emptyList(),
    val continueWatching: List<WatchProgress> = emptyList(),
    val recentMovies: List<VodItem> = emptyList(),
    val recentSeries: List<Series> = emptyList(),
    val isLoading: Boolean = true
)
