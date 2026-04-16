package com.rooftop.ui.tv.vod

import com.rooftop.domain.model.VodItem
import com.rooftop.domain.model.WatchProgress

data class VodUiState(
    val items: List<VodItem> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String? = null,
    val continueWatching: List<WatchProgress> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
