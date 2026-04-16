package com.rooftop.ui.tv.epg

import com.rooftop.domain.model.Channel
import com.rooftop.domain.model.Programme

data class EpgUiState(
    val channels: List<Channel> = emptyList(),
    // epgChannelId -> programmes sorted by start
    val programmes: Map<String, List<Programme>> = emptyMap(),
    val selectedProgramme: Programme? = null,
    val nowMs: Long = System.currentTimeMillis(),
    val isLoading: Boolean = true,
    val error: String? = null
)
