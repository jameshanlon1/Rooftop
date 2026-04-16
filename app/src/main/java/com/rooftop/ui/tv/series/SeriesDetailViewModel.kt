package com.rooftop.ui.tv.series

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rooftop.domain.model.Episode
import com.rooftop.domain.repository.PlaylistRepository
import com.rooftop.domain.repository.SeriesRepository
import com.rooftop.domain.repository.WatchProgressRepository
import com.rooftop.player.PlaybackRequest
import com.rooftop.player.PlaybackRequestHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeriesDetailViewModel @Inject constructor(
    private val seriesRepository: SeriesRepository,
    private val playlistRepository: PlaylistRepository,
    private val watchProgressRepository: WatchProgressRepository,
    private val playbackRequestHolder: PlaybackRequestHolder
) : ViewModel() {

    private val _uiState = MutableStateFlow(SeriesDetailUiState())
    val uiState: StateFlow<SeriesDetailUiState> = _uiState.asStateFlow()

    fun load(seriesId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val playlist = playlistRepository.getPlaylists().first()
                .firstOrNull { it.xtreamBaseUrl != null }
                ?: playlistRepository.getPlaylists().first().firstOrNull()

            val detail = playlist?.let { seriesRepository.getSeriesDetail(seriesId, it) }
            val firstSeason = detail?.seasons?.keys?.minOrNull() ?: 1

            _uiState.update {
                it.copy(detail = detail, selectedSeason = firstSeason, isLoading = false)
            }
        }
    }

    fun selectSeason(season: Int) {
        _uiState.update { it.copy(selectedSeason = season) }
    }

    fun prepareEpisodePlayback(episode: Episode) {
        viewModelScope.launch {
            val seriesName = _uiState.value.detail?.series?.name ?: ""
            val contentId = "episode_${episode.seriesId}_${episode.season}_${episode.episodeNum}"
            val savedPosition = watchProgressRepository.getProgress(contentId)?.positionMs ?: 0L

            playbackRequestHolder.request = PlaybackRequest(
                contentId = contentId,
                streamUrl = episode.streamUrl,
                title = "$seriesName — S${episode.season}E${episode.episodeNum}: ${episode.title}",
                posterUrl = _uiState.value.detail?.series?.coverUrl,
                savedPositionMs = savedPosition,
                contentType = "EPISODE"
            )
        }
    }
}
