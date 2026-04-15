package com.rooftop.domain.usecase

import com.rooftop.domain.model.Playlist
import com.rooftop.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPlaylistsUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository
) {
    operator fun invoke(): Flow<List<Playlist>> = playlistRepository.getPlaylists()
}
