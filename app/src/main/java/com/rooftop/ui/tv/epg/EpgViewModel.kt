package com.rooftop.ui.tv.epg

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rooftop.domain.model.Programme
import com.rooftop.domain.repository.ChannelRepository
import com.rooftop.domain.repository.EpgRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class EpgViewModel @Inject constructor(
    private val channelRepository: ChannelRepository,
    private val epgRepository: EpgRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EpgUiState())
    val uiState: StateFlow<EpgUiState> = _uiState.asStateFlow()

    init {
        loadEpg()
    }

    private fun loadEpg() {
        viewModelScope.launch {
            val nowMs = System.currentTimeMillis()
            val from = nowMs - TimeUnit.HOURS.toMillis(1)
            val to = nowMs + TimeUnit.DAYS.toMillis(7)

            channelRepository.getChannels().collect { channels ->
                val epgIds = channels.mapNotNull { it.epgChannelId }.distinct()

                // Build a combined flow of all channels' programmes
                if (epgIds.isEmpty()) {
                    _uiState.update {
                        it.copy(channels = channels, programmes = emptyMap(), isLoading = false)
                    }
                    return@collect
                }

                val flows = epgIds.map { id ->
                    epgRepository.getProgrammesForChannel(id, from, to)
                }

                combine(flows) { arrays ->
                    epgIds.zip(arrays.toList()).associate { (id, progs) -> id to progs }
                }.collect { programmes ->
                    _uiState.update {
                        it.copy(
                            channels = channels,
                            programmes = programmes,
                            nowMs = System.currentTimeMillis(),
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun refreshEpg(url: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            epgRepository.refreshEpg(url)
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun selectProgramme(programme: Programme?) {
        _uiState.update { it.copy(selectedProgramme = programme) }
    }

    fun scheduleReminder(programme: Programme, channelName: String) {
        viewModelScope.launch {
            epgRepository.scheduleReminder(programme, channelName)
        }
    }
}
