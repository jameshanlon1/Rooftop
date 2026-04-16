package com.rooftop.ui.tv.vod

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rooftop.data.local.dao.VodDao
import com.rooftop.data.local.mapper.toDomain
import com.rooftop.data.preferences.PreferencesManager
import com.rooftop.domain.repository.PlaylistRepository
import com.rooftop.domain.repository.VodRepository
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
class VodDetailViewModel @Inject constructor(
    private val vodRepository: VodRepository,
    private val vodDao: VodDao,
    private val playlistRepository: PlaylistRepository,
    private val watchProgressRepository: WatchProgressRepository,
    private val preferencesManager: PreferencesManager,
    private val playbackRequestHolder: PlaybackRequestHolder
) : ViewModel() {

    private val _uiState = MutableStateFlow(VodDetailUiState())
    val uiState: StateFlow<VodDetailUiState> = _uiState.asStateFlow()

    fun load(vodId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val playlist = playlistRepository.getPlaylists().first()
                .firstOrNull { it.xtreamBaseUrl != null }
                ?: playlistRepository.getPlaylists().first().firstOrNull()

            val savedPosition = watchProgressRepository.getProgress("vod_$vodId")?.positionMs ?: 0L
            val info = playlist?.let { vodRepository.getVodInfo(vodId, it) }
            val isFav = preferencesManager.favouriteIds.first().contains("vod_$vodId")

            // Load recommended: same category, up to 20 items excluding this one
            val recommended = if (info != null && !info.genre.isNullOrBlank()) {
                vodDao.getByCategory(info.genre!!).first()
                    .filter { it.id != vodId }
                    .take(20)
                    .map { it.toDomain() }
            } else {
                vodDao.search("").take(20).map { it.toDomain() }
            }

            _uiState.update {
                it.copy(
                    vodInfo = info,
                    savedPositionMs = savedPosition,
                    isLoading = false,
                    isFavourite = isFav,
                    recommendedVod = recommended
                )
            }
        }
    }

    fun toggleFavourite() {
        val vodId = _uiState.value.vodInfo?.item?.id ?: return
        viewModelScope.launch {
            preferencesManager.toggleFavourite("vod_$vodId")
            val isFav = preferencesManager.favouriteIds.first().contains("vod_$vodId")
            _uiState.update { it.copy(isFavourite = isFav) }
        }
    }

    fun preparePlayback() {
        val info = _uiState.value.vodInfo ?: return
        playbackRequestHolder.request = PlaybackRequest(
            contentId = "vod_${info.item.id}",
            streamUrl = info.item.streamUrl,
            title = info.item.name,
            posterUrl = info.item.posterUrl,
            savedPositionMs = _uiState.value.savedPositionMs,
            contentType = "VOD"
        )
    }
}
