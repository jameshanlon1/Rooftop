package com.rooftop.data.repository

import com.rooftop.data.local.dao.WatchProgressDao
import com.rooftop.data.local.mapper.toDomain
import com.rooftop.data.local.mapper.toEntity
import com.rooftop.domain.model.WatchProgress
import com.rooftop.domain.repository.WatchProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WatchProgressRepositoryImpl @Inject constructor(
    private val watchProgressDao: WatchProgressDao
) : WatchProgressRepository {

    override fun getRecentProgress(limit: Int): Flow<List<WatchProgress>> =
        watchProgressDao.getRecent(limit).map { it.map { e -> e.toDomain() } }

    override suspend fun saveProgress(progress: WatchProgress) {
        watchProgressDao.upsert(progress.toEntity())
    }

    override suspend fun getProgress(contentId: String): WatchProgress? =
        watchProgressDao.getById(contentId)?.toDomain()

    override suspend fun deleteProgress(contentId: String) {
        watchProgressDao.delete(contentId)
    }
}
