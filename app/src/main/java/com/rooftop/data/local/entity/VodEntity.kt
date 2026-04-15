package com.rooftop.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vod_items")
data class VodEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0L,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "stream_url") val streamUrl: String,
    @ColumnInfo(name = "poster_url") val posterUrl: String?,
    @ColumnInfo(name = "category") val category: String?,
    @ColumnInfo(name = "rating") val rating: String?,
    @ColumnInfo(name = "year") val year: String?,
    @ColumnInfo(name = "playlist_id") val playlistId: Long
)
