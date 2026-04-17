package com.rooftop.ui.tv.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rooftop.domain.repository.SeriesRepository
import com.rooftop.domain.repository.VodRepository
import com.rooftop.domain.repository.WatchProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val vodRepository: VodRepository,
    private val seriesRepository: SeriesRepository,
    private val watchProgressRepository: WatchProgressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observe()
    }

    private fun observe() {
        viewModelScope.launch {
            combine(
                watchProgressRepository.getRecentProgress(20),
                vodRepository.getVodItems(),
                seriesRepository.getSeriesItems()
            ) { progress, vod, series -> Triple(progress, vod, series) }
            .collect { (progress, vod, series) ->
                // Build up to 8 featured items: interleave series and VOD (§2.1)
                val seriesFeatured = series
                    .filter { !it.coverUrl.isNullOrBlank() }
                    .take(4)
                    .map { FeaturedItem(it.name, it.coverUrl, it.category ?: "", seriesId = it.id) }
                val vodFeatured = vod
                    .filter { !it.posterUrl.isNullOrBlank() }
                    .take(4)
                    .map { FeaturedItem(it.name, it.posterUrl, it.category ?: "", vodId = it.id) }

                val featured = buildList {
                    val s = seriesFeatured.iterator()
                    val v = vodFeatured.iterator()
                    while ((s.hasNext() || v.hasNext()) && size < 8) {
                        if (s.hasNext()) add(s.next())
                        if (v.hasNext() && size < 8) add(v.next())
                    }
                }

                _uiState.update { state ->
                    state.copy(
                        featuredItems = featured,
                        continueWatching = progress,
                        recentMovies = vod.take(20),
                        recentSeries = series.take(20),
                        isLoading = false
                    )
                }
            }
        }
    }
}
