package com.rooftop.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rooftop.data.local.entity.ProgrammeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgrammeDao {

    @Query("""
        SELECT * FROM programmes
        WHERE channel_id = :channelId AND stop_ms > :from AND start_ms < :to
        ORDER BY start_ms
    """)
    fun getProgrammesForChannel(channelId: String, from: Long, to: Long): Flow<List<ProgrammeEntity>>

    @Query("""
        SELECT * FROM programmes
        WHERE channel_id = :channelId AND start_ms <= :nowMs AND stop_ms > :nowMs
        LIMIT 1
    """)
    suspend fun getNow(channelId: String, nowMs: Long): ProgrammeEntity?

    @Query("""
        SELECT * FROM programmes
        WHERE channel_id = :channelId AND start_ms > :nowMs
        ORDER BY start_ms LIMIT 1
    """)
    suspend fun getNext(channelId: String, nowMs: Long): ProgrammeEntity?

    @Query("SELECT * FROM programmes WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): ProgrammeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(programmes: List<ProgrammeEntity>)

    @Update
    suspend fun update(programme: ProgrammeEntity)

    @Query("DELETE FROM programmes WHERE stop_ms < :beforeMs")
    suspend fun deleteOld(beforeMs: Long)
}
