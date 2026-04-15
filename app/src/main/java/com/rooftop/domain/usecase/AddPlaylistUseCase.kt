package com.rooftop.domain.usecase

import com.rooftop.domain.model.Playlist
import com.rooftop.domain.model.PlaylistType
import com.rooftop.domain.model.Result
import com.rooftop.domain.repository.PlaylistRepository
import javax.inject.Inject

class AddPlaylistUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(playlist: Playlist): Result<Long> {
        if (playlist.name.isBlank()) {
            return Result.Error(IllegalArgumentException("Playlist name cannot be empty"))
        }
        when (playlist.type) {
            PlaylistType.M3U -> {
                if (playlist.url.isNullOrBlank()) {
                    return Result.Error(IllegalArgumentException("M3U URL cannot be empty"))
                }
            }
            PlaylistType.XTREAM -> {
                if (playlist.xtreamBaseUrl.isNullOrBlank() ||
                    playlist.xtreamUsername.isNullOrBlank() ||
                    playlist.xtreamPassword.isNullOrBlank()
                ) {
                    return Result.Error(IllegalArgumentException("Xtream credentials are incomplete"))
                }
            }
        }
        return playlistRepository.addPlaylist(playlist)
    }
}
