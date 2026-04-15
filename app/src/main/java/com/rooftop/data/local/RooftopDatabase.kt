package com.rooftop.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rooftop.data.local.dao.ChannelDao
import com.rooftop.data.local.dao.PlaylistDao
import com.rooftop.data.local.dao.SeriesDao
import com.rooftop.data.local.dao.VodDao
import com.rooftop.data.local.entity.ChannelEntity
import com.rooftop.data.local.entity.PlaylistEntity
import com.rooftop.data.local.entity.SeriesEntity
import com.rooftop.data.local.entity.VodEntity

@Database(
    entities = [
        ChannelEntity::class,
        VodEntity::class,
        SeriesEntity::class,
        PlaylistEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class RooftopDatabase : RoomDatabase() {
    abstract fun channelDao(): ChannelDao
    abstract fun vodDao(): VodDao
    abstract fun seriesDao(): SeriesDao
    abstract fun playlistDao(): PlaylistDao
}
