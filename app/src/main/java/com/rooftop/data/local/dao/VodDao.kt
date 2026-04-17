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

    @Query("SELECT * FROM vod_items WHERE category = :category ORDER BY name")
    fun getByCategory(category: String): Flow<List<VodEntity>>

    @Query("SELECT DISTINCT category FROM vod_items WHERE category IS NOT NULL ORDER BY category")
    fun getCategories(): Flow<List<String>>

    @Query("SELECT * FROM vod_items WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): VodEntity?

    @Query("SELECT * FROM vod_items WHERE name LIKE '%' || :query || '%' ORDER BY name LIMIT 30")
    suspend fun search(query: String): List<VodEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<VodEntity>)

    @Query("DELETE FROM vod_items WHERE playlist_id = :playlistId")
    suspend fun deleteByPlaylist(playlistId: Long)
}
