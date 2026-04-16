package com.rooftop.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "programmes",
    indices = [Index("channel_id"), Index("start_ms"), Index("stop_ms")]
)
data class ProgrammeEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0L,
    @ColumnInfo(name = "channel_id") val channelId: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "start_ms") val startMs: Long,
    @ColumnInfo(name = "stop_ms") val stopMs: Long,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "category") val category: String?,
    @ColumnInfo(name = "icon_url") val iconUrl: String?,
    @ColumnInfo(name = "has_reminder") val hasReminder: Boolean = false
)
