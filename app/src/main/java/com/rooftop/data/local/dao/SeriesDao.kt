package com.rooftop.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rooftop.data.local.entity.SeriesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SeriesDao {
    @Query("SELECT * FROM series ORDER BY name")
    fun getAll(): Flow<List<SeriesEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<SeriesEntity>)

    @Query("DELETE FROM series WHERE playlist_id = :playlistId")
    suspend fun deleteByPlaylist(playlistId: Long)
}
