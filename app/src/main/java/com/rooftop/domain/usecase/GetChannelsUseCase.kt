package com.rooftop.domain.usecase

import com.rooftop.domain.model.Channel
import com.rooftop.domain.repository.ChannelRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChannelsUseCase @Inject constructor(
    private val channelRepository: ChannelRepository
) {
    operator fun invoke(groupFilter: String? = null): Flow<List<Channel>> =
        if (groupFilter != null) channelRepository.getChannelsByGroup(groupFilter)
        else channelRepository.getChannels()
}
