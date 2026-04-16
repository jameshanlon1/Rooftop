package com.rooftop.ui.tv.player

import com.rooftop.domain.model.Channel

data class PlayerUiState(
    val currentChannel: Channel? = null,
    val channels: List<Channel> = emptyList(),
    val currentIndex: Int = 0,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = true,
    val isControlsVisible: Boolean = true,
    val error: String? = null,
    val audioTracks: List<TrackInfo> = emptyList(),
    val subtitleTracks: List<TrackInfo> = emptyList(),
    val showTrackSelector: Boolean = false
)

data class TrackInfo(
    val groupIndex: Int,
    val trackIndex: Int,
    val label: String,
    val isSelected: Boolean
)
