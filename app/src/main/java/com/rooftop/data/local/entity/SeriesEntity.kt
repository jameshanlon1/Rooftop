package com.rooftop.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "series")
data class SeriesEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0L,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "cover_url") val coverUrl: String?,
    @ColumnInfo(name = "category") val category: String?,
    @ColumnInfo(name = "rating") val rating: String?,
    @ColumnInfo(name = "playlist_id") val playlistId: Long
)
