package com.rooftop.ui.tv.series

import com.rooftop.domain.model.Series

data class SeriesUiState(
    val items: List<Series> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)
