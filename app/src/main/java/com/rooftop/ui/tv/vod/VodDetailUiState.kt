package com.rooftop.ui.tv.vod

import com.rooftop.domain.model.VodInfo
import com.rooftop.domain.model.VodItem

data class VodDetailUiState(
    val vodInfo: VodInfo? = null,
    val savedPositionMs: Long = 0L,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isFavourite: Boolean = false,
    val recommendedVod: List<VodItem> = emptyList()
)
