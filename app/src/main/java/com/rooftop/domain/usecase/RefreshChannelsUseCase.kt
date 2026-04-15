package com.rooftop.domain.usecase

import com.rooftop.domain.model.Result
import com.rooftop.domain.repository.ChannelRepository
import javax.inject.Inject

class RefreshChannelsUseCase @Inject constructor(
    private val channelRepository: ChannelRepository
) {
    suspend operator fun invoke(playlistId: Long): Result<Unit> =
        channelRepository.refreshChannels(playlistId)
}
