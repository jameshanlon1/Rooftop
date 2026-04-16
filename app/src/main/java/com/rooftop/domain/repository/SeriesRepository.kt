package com.rooftop.domain.repository

import com.rooftop.domain.model.Playlist
import com.rooftop.domain.model.Result
import com.rooftop.domain.model.Series
import com.rooftop.domain.model.SeriesDetail
import kotlinx.coroutines.flow.Flow

interface SeriesRepository {
    fun getSeriesItems(): Flow<List<Series>>
    fun getCategories(): Flow<List<String>>
    fun getSeriesByCategory(category: String): Flow<List<Series>>
    suspend fun getSeriesDetail(seriesId: Long, playlist: Playlist): SeriesDetail?
    suspend fun refreshSeries(playlist: Playlist): Result<Unit>
}
