package com.rooftop.domain.repository

import com.rooftop.domain.model.Programme
import com.rooftop.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface EpgRepository {
    suspend fun refreshEpg(url: String): Result<Unit>
    fun getProgrammesForChannel(channelId: String, from: Long, to: Long): Flow<List<Programme>>
    suspend fun getNow(channelId: String, nowMs: Long): Programme?
    suspend fun getNext(channelId: String, nowMs: Long): Programme?
    suspend fun scheduleReminder(programme: Programme, channelName: String)
    suspend fun cancelReminder(programmeId: Long)
    suspend fun clearOldProgrammes(beforeMs: Long)
}
