package com.rooftop.ui.tv.vod

import com.rooftop.domain.model.VodInfo

data class VodDetailUiState(
    val vodInfo: VodInfo? = null,
    val savedPositionMs: Long = 0L,
    val isLoading: Boolean = true,
    val error: String? = null
)
