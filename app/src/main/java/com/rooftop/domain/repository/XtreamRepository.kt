package com.rooftop.domain.repository

import com.rooftop.domain.model.Channel
import com.rooftop.domain.model.Playlist
import com.rooftop.domain.model.Result
import com.rooftop.domain.model.Series
import com.rooftop.domain.model.VodItem

interface XtreamRepository {
    suspend fun getLiveStreams(playlist: Playlist): Result<List<Channel>>
    suspend fun getVodStreams(playlist: Playlist): Result<List<VodItem>>
    suspend fun getSeries(playlist: Playlist): Result<List<Series>>
}
