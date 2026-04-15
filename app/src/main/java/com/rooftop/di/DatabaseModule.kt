package com.rooftop.di

import android.content.Context
import androidx.room.Room
import com.rooftop.data.local.RooftopDatabase
import com.rooftop.data.local.dao.ChannelDao
import com.rooftop.data.local.dao.PlaylistDao
import com.rooftop.data.local.dao.SeriesDao
import com.rooftop.data.local.dao.VodDao
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
        Room.databaseBuilder(context, RooftopDatabase::class.java, "rooftop.db").build()

    @Provides fun provideChannelDao(db: RooftopDatabase): ChannelDao = db.channelDao()
    @Provides fun provideVodDao(db: RooftopDatabase): VodDao = db.vodDao()
    @Provides fun provideSeriesDao(db: RooftopDatabase): SeriesDao = db.seriesDao()
    @Provides fun providePlaylistDao(db: RooftopDatabase): PlaylistDao = db.playlistDao()
}
