package com.rooftop.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.rooftop.data.local.RooftopDatabase
import com.rooftop.data.local.dao.ChannelDao
import com.rooftop.data.local.dao.PlaylistDao
import com.rooftop.data.local.dao.ProgrammeDao
import com.rooftop.data.local.dao.SeriesDao
import com.rooftop.data.local.dao.VodDao
import com.rooftop.data.local.dao.WatchProgressDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): RooftopDatabase =
        Room.databaseBuilder(context, RooftopDatabase::class.java, "rooftop.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideChannelDao(db: RooftopDatabase): ChannelDao = db.channelDao()
    @Provides fun provideVodDao(db: RooftopDatabase): VodDao = db.vodDao()
    @Provides fun provideSeriesDao(db: RooftopDatabase): SeriesDao = db.seriesDao()
    @Provides fun providePlaylistDao(db: RooftopDatabase): PlaylistDao = db.playlistDao()
    @Provides fun provideProgrammeDao(db: RooftopDatabase): ProgrammeDao = db.programmeDao()
    @Provides fun provideWatchProgressDao(db: RooftopDatabase): WatchProgressDao = db.watchProgressDao()

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager =
        WorkManager.getInstance(context)
}
