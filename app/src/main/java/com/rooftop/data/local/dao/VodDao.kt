package com.rooftop.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rooftop.data.local.entity.VodEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VodDao {
    @Query("SELECT * FROM vod_items ORDER BY name")
    fun getAll(): Flow<List<VodEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<VodEntity>)

    @Query("DELETE FROM vod_items WHERE playlist_id = :playlistId")
    suspend fun deleteByPlaylist(playlistId: Long)
}
