package com.rooftop.di

import com.rooftop.data.repository.ChannelRepositoryImpl
import com.rooftop.data.repository.EpgRepositoryImpl
import com.rooftop.data.repository.PlaylistRepositoryImpl
import com.rooftop.data.repository.XtreamRepositoryImpl
import com.rooftop.domain.repository.ChannelRepository
import com.rooftop.domain.repository.EpgRepository
import com.rooftop.domain.repository.PlaylistRepository
import com.rooftop.domain.repository.XtreamRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindChannelRepository(impl: ChannelRepositoryImpl): ChannelRepository

    @Binds @Singleton
    abstract fun bindPlaylistRepository(impl: PlaylistRepositoryImpl): PlaylistRepository

    @Binds @Singleton
    abstract fun bindXtreamRepository(impl: XtreamRepositoryImpl): XtreamRepository

    @Binds @Singleton
    abstract fun bindEpgRepository(impl: EpgRepositoryImpl): EpgRepository
}
