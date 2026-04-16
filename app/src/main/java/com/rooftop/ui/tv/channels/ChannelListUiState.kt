package com.rooftop.ui.tv.channels

import com.rooftop.domain.model.Channel
import com.rooftop.domain.model.Programme

data class ChannelListUiState(
    val channels: List<Channel> = emptyList(),
    val groups: List<String> = emptyList(),
    val selectedGroup: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    // keyed by Channel.epgChannelId
    val nowPlaying: Map<String, Programme?> = emptyMap(),
    val nextUp: Map<String, Programme?> = emptyMap()
)
