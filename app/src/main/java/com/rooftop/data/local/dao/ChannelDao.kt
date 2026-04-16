package com.rooftop.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rooftop.data.local.entity.ChannelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelDao {
    @Query("SELECT * FROM channels ORDER BY group_title, name")
    fun getAll(): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels WHERE group_title = :group ORDER BY name")
    fun getByGroup(group: String): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels WHERE playlist_id = :playlistId")
    suspend fun getByPlaylist(playlistId: Long): List<ChannelEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(channels: List<ChannelEntity>)

    @Query("SELECT * FROM channels WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): ChannelEntity?

    @Query("SELECT COUNT(*) FROM channels")
    suspend fun getCount(): Int

    @Query("DELETE FROM channels WHERE playlist_id = :playlistId")
    suspend fun deleteByPlaylist(playlistId: Long)
}
