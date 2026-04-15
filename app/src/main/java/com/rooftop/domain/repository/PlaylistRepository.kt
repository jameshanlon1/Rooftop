package com.rooftop.domain.repository

import com.rooftop.domain.model.Playlist
import com.rooftop.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun getPlaylistById(id: Long): Playlist?
    suspend fun addPlaylist(playlist: Playlist): Result<Long>
    suspend fun deletePlaylist(id: Long)
}
