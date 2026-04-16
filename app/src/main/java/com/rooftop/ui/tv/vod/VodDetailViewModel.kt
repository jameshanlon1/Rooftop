package com.rooftop.ui.tv.vod

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val playlistRepository: PlaylistRepository,
    private val watchProgressRepository: WatchProgressRepository,
    private val playbackRequestHolder: PlaybackRequestHolder
) : ViewModel() {

    private val _uiState = MutableStateFlow(VodDetailUiState())
    val uiState: StateFlow<VodDetailUiState> = _uiState.asStateFlow()

    fun load(vodId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Use first Xtream playlist found, or any playlist
            val playlist = playlistRepository.getPlaylists().first()
                .firstOrNull { it.xtreamBaseUrl != null }
                ?: playlistRepository.getPlaylists().first().firstOrNull()

            val savedPosition = watchProgressRepository.getProgress("vod_$vodId")?.positionMs ?: 0L
            val info = playlist?.let { vodRepository.getVodInfo(vodId, it) }

            _uiState.update {
                it.copy(vodInfo = info, savedPositionMs = savedPosition, isLoading = false)
            }
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
