package com.rooftop.domain.repository

import com.rooftop.domain.model.Playlist
import com.rooftop.domain.model.Result
import com.rooftop.domain.model.VodInfo
import com.rooftop.domain.model.VodItem
import kotlinx.coroutines.flow.Flow

interface VodRepository {
    fun getVodItems(): Flow<List<VodItem>>
    fun getCategories(): Flow<List<String>>
    fun getVodByCategory(category: String): Flow<List<VodItem>>
    suspend fun getVodInfo(vodId: Long, playlist: Playlist): VodInfo?
    suspend fun refreshVod(playlist: Playlist): Result<Unit>
}
