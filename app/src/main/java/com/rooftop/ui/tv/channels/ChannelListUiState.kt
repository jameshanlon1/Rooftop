package com.rooftop.ui.tv.channels

import com.rooftop.domain.model.Channel

data class ChannelListUiState(
    val channels: List<Channel> = emptyList(),
    val groups: List<String> = emptyList(),
    val selectedGroup: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
