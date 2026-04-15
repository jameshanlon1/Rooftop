package com.rooftop.domain.repository

import com.rooftop.domain.model.Channel
import com.rooftop.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface ChannelRepository {
    fun getChannels(): Flow<List<Channel>>
    fun getChannelsByGroup(group: String): Flow<List<Channel>>
    suspend fun refreshChannels(playlistId: Long): Result<Unit>
    suspend fun clearChannels(playlistId: Long)
}
