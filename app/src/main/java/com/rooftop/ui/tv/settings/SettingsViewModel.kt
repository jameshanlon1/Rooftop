package com.rooftop.ui.tv.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rooftop.data.preferences.PreferencesManager
import com.rooftop.domain.model.Playlist
import com.rooftop.domain.model.PlaylistType
import com.rooftop.domain.model.Result
import com.rooftop.domain.repository.ChannelRepository
import com.rooftop.domain.repository.EpgRepository
import com.rooftop.domain.repository.PlaylistRepository
import com.rooftop.domain.repository.SeriesRepository
import com.rooftop.domain.repository.VodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository,
    private val channelRepository: ChannelRepository,
    private val vodRepository: VodRepository,
    private val seriesRepository: SeriesRepository,
    private val epgRepository: EpgRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            playlistRepository.getPlaylists().collect { playlists ->
                _uiState.update { it.copy(playlists = playlists, activePlaylist = playlists.firstOrNull()) }
            }
        }
        viewModelScope.launch {
            preferencesManager.epgUrl.collect { url ->
                _uiState.update { it.copy(epgUrl = url) }
            }
        }
    }

    // Add playlist dialog
    fun showAddDialog() = _uiState.update { it.copy(showAddDialog = true) }
    fun hideAddDialog() = _uiState.update {
        it.copy(showAddDialog = false, addName = "", addUrl = "",
            addXtreamBase = "", addXtreamUser = "", addXtreamPass = "")
    }

    fun onAddTypeChanged(type: PlaylistType) = _uiState.update { it.copy(addType = type) }
    fun onAddNameChanged(v: String) = _uiState.update { it.copy(addName = v) }
    fun onAddUrlChanged(v: String) = _uiState.update { it.copy(addUrl = v) }
    fun onAddXtreamBaseChanged(v: String) = _uiState.update { it.copy(addXtreamBase = v) }
    fun onAddXtreamUserChanged(v: String) = _uiState.update { it.copy(addXtreamUser = v) }
    fun onAddXtreamPassChanged(v: String) = _uiState.update { it.copy(addXtreamPass = v) }
    fun onEpgUrlChanged(v: String) = _uiState.update { it.copy(epgUrl = v) }

    fun saveEpgUrl() {
        viewModelScope.launch { preferencesManager.setEpgUrl(_uiState.value.epgUrl) }
    }

    fun addPlaylist() {
        val state = _uiState.value
        if (state.addName.isBlank()) return
        val playlist = when (state.addType) {
            PlaylistType.M3U -> Playlist(
                name = state.addName,
                type = PlaylistType.M3U,
                url = state.addUrl
            )
            PlaylistType.XTREAM -> Playlist(
                name = state.addName,
                type = PlaylistType.XTREAM,
                xtreamBaseUrl = state.addXtreamBase,
                xtreamUsername = state.addXtreamUser,
                xtreamPassword = state.addXtreamPass
            )
        }
        viewModelScope.launch {
            playlistRepository.addPlaylist(playlist)
            hideAddDialog()
        }
    }

    fun deletePlaylist(id: Long) {
        viewModelScope.launch { playlistRepository.deletePlaylist(id) }
    }

    fun syncAll() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, syncStatus = null) }
            val playlists = playlistRepository.getPlaylists().first()
            var errors = 0

            playlists.forEach { playlist ->
                if (channelRepository.refreshChannels(playlist.id) is Result.Error) errors++
                if (playlist.type == PlaylistType.XTREAM) {
                    if (vodRepository.refreshVod(playlist) is Result.Error) errors++
                    if (seriesRepository.refreshSeries(playlist) is Result.Error) errors++
                }
            }

            val epgUrl = preferencesManager.epgUrl.first()
            if (epgUrl.isNotBlank()) {
                if (epgRepository.refreshEpg(epgUrl) is Result.Error) errors++
            }

            _uiState.update {
                it.copy(
                    isSyncing = false,
                    syncStatus = if (errors == 0) "Synced" else "Done — $errors error(s)"
                )
            }
        }
    }

    // §3.1 Player toggles
    fun onAutoPlayNextEpisodeToggled() =
        _uiState.update { it.copy(autoPlayNextEpisode = !it.autoPlayNextEpisode) }

    // §3.2 Interface toggles
    fun onBlurUnseenEpisodesToggled() =
        _uiState.update { it.copy(blurUnseenEpisodes = !it.blurUnseenEpisodes) }
    fun onNewEpisodeAlertsToggled() =
        _uiState.update { it.copy(newEpisodeAlerts = !it.newEpisodeAlerts) }
    fun onShowWhatsNewToggled() =
        _uiState.update { it.copy(showWhatsNewOnUpdate = !it.showWhatsNewOnUpdate) }
}
