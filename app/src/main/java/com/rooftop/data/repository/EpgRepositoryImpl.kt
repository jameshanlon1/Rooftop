package com.rooftop.data.repository

import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.rooftop.data.local.dao.ProgrammeDao
import com.rooftop.data.local.mapper.toDomain
import com.rooftop.data.local.mapper.toEntity
import com.rooftop.domain.model.Programme
import com.rooftop.domain.model.Result
import com.rooftop.domain.repository.EpgRepository
import com.rooftop.epg.XmlTvParser
import com.rooftop.worker.ReminderWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class EpgRepositoryImpl @Inject constructor(
    private val programmeDao: ProgrammeDao,
    private val xmlTvParser: XmlTvParser,
    private val okHttpClient: OkHttpClient,
    private val workManager: WorkManager
) : EpgRepository {

    override suspend fun refreshEpg(url: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(url).build()
            val response = okHttpClient.newCall(request).execute()
            val body = response.body ?: return@withContext Result.Error(Exception("Empty EPG response"))
            val programmes = xmlTvParser.parse(body.byteStream())
            programmeDao.insertAll(programmes.map { it.toEntity() })
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getProgrammesForChannel(channelId: String, from: Long, to: Long): Flow<List<Programme>> =
        programmeDao.getProgrammesForChannel(channelId, from, to)
            .map { it.map { e -> e.toDomain() } }

    override suspend fun getNow(channelId: String, nowMs: Long): Programme? =
        programmeDao.getNow(channelId, nowMs)?.toDomain()

    override suspend fun getNext(channelId: String, nowMs: Long): Programme? =
        programmeDao.getNext(channelId, nowMs)?.toDomain()

    override suspend fun scheduleReminder(programme: Programme, channelName: String) {
        val delayMs = programme.start - System.currentTimeMillis() - REMINDER_OFFSET_MS
        if (delayMs < 0) return

        val entity = programmeDao.getById(programme.id) ?: return
        programmeDao.update(entity.copy(hasReminder = true))

        val data = Data.Builder()
            .putString(ReminderWorker.KEY_PROGRAMME_TITLE, programme.title)
            .putString(ReminderWorker.KEY_CHANNEL_NAME, channelName)
            .build()

        val work = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag("reminder_${programme.id}")
            .build()

        workManager.enqueue(work)
    }

    override suspend fun cancelReminder(programmeId: Long) {
        val entity = programmeDao.getById(programmeId) ?: return
        programmeDao.update(entity.copy(hasReminder = false))
        workManager.cancelAllWorkByTag("reminder_$programmeId")
    }

    override suspend fun clearOldProgrammes(beforeMs: Long) {
        programmeDao.deleteOld(beforeMs)
    }

    companion object {
        private const val REMINDER_OFFSET_MS = 5 * 60 * 1000L // notify 5 min before
    }
}
