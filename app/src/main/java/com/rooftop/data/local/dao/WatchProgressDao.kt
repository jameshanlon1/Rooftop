package com.rooftop.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rooftop.data.local.entity.WatchProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchProgressDao {

    @Query("SELECT * FROM watch_progress ORDER BY updated_at DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<WatchProgressEntity>>

    @Query("SELECT * FROM watch_progress WHERE content_id = :contentId LIMIT 1")
    suspend fun getById(contentId: String): WatchProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(progress: WatchProgressEntity)

    @Query("DELETE FROM watch_progress WHERE content_id = :contentId")
    suspend fun delete(contentId: String)

    @Query("DELETE FROM watch_progress WHERE updated_at < :beforeMs")
    suspend fun deleteOld(beforeMs: Long)
}
