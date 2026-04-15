package com.rooftop.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0L,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "url") val url: String?,
    @ColumnInfo(name = "xtream_base_url") val xtreamBaseUrl: String?,
    @ColumnInfo(name = "xtream_username") val xtreamUsername: String?,
    @ColumnInfo(name = "xtream_password") val xtreamPassword: String?
)
