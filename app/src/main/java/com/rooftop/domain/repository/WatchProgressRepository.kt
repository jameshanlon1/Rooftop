package com.rooftop.domain.repository

import com.rooftop.domain.model.WatchProgress
import kotlinx.coroutines.flow.Flow

interface WatchProgressRepository {
    fun getRecentProgress(limit: Int = 20): Flow<List<WatchProgress>>
    suspend fun saveProgress(progress: WatchProgress)
    suspend fun getProgress(contentId: String): WatchProgress?
    suspend fun deleteProgress(contentId: String)
}
