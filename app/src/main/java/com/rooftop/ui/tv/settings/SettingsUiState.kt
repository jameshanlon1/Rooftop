package com.rooftop.ui.tv.settings

import com.rooftop.domain.model.Playlist
import com.rooftop.domain.model.PlaylistType

data class SettingsUiState(
    // Playlists
    val playlists: List<Playlist> = emptyList(),
    val activePlaylist: Playlist? = null,
    val epgUrl: String = "",
    // Sync
    val isSyncing: Boolean = false,
    val syncStatus: String? = null,
    // Add playlist dialog
    val showAddDialog: Boolean = false,
    val addType: PlaylistType = PlaylistType.M3U,
    val addName: String = "",
    val addUrl: String = "",
    val addXtreamBase: String = "",
    val addXtreamUser: String = "",
    val addXtreamPass: String = "",
    // §3.1 Player settings
    val autoPlayNextEpisode: Boolean = true,
    // §3.2 Interface settings
    val blurUnseenEpisodes: Boolean = false,
    val newEpisodeAlerts: Boolean = false,
    val showWhatsNewOnUpdate: Boolean = true
)
