package com.rooftop.data.repository

import com.rooftop.data.local.dao.PlaylistDao
import com.rooftop.data.local.mapper.toDomain
import com.rooftop.data.local.mapper.toEntity
import com.rooftop.domain.model.Playlist
import com.rooftop.domain.model.Result
import com.rooftop.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PlaylistRepositoryImpl @Inject constructor(
    private val playlistDao: PlaylistDao
) : PlaylistRepository {

    override fun getPlaylists(): Flow<List<Playlist>> =
        playlistDao.getAll().map { it.map { entity -> entity.toDomain() } }

    override suspend fun getPlaylistById(id: Long): Playlist? =
        playlistDao.getById(id)?.toDomain()

    override suspend fun addPlaylist(playlist: Playlist): Result<Long> =
        try {
            Result.Success(playlistDao.insert(playlist.toEntity()))
        } catch (e: Exception) {
            Result.Error(e)
        }

    override suspend fun deletePlaylist(id: Long) {
        playlistDao.deleteById(id)
    }
}
