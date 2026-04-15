package com.rooftop.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "channels")
data class ChannelEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0L,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "stream_url") val streamUrl: String,
    @ColumnInfo(name = "logo_url") val logoUrl: String?,
    @ColumnInfo(name = "group_title") val groupTitle: String?,
    @ColumnInfo(name = "epg_channel_id") val epgChannelId: String?,
    @ColumnInfo(name = "stream_type") val streamType: String = "LIVE",
    @ColumnInfo(name = "playlist_id") val playlistId: Long
)
