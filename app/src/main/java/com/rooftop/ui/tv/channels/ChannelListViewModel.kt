package com.rooftop.ui.tv.channels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rooftop.domain.model.Result
import com.rooftop.domain.repository.EpgRepository
import com.rooftop.domain.usecase.GetChannelsUseCase
import com.rooftop.domain.usecase.RefreshChannelsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChannelListViewModel @Inject constructor(
    private val getChannelsUseCase: GetChannelsUseCase,
    private val refreshChannelsUseCase: RefreshChannelsUseCase,
    private val epgRepository: EpgRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChannelListUiState(isLoading = true))
    val uiState: StateFlow<ChannelListUiState> = _uiState.asStateFlow()

    private var channelJob: Job? = null

    init {
        observeChannels()
    }

    private fun observeChannels() {
        channelJob?.cancel()
        channelJob = viewModelScope.launch {
            getChannelsUseCase(groupFilter = _uiState.value.selectedGroup)
                .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { channels ->
                    val groups = channels.mapNotNull { it.groupTitle }.distinct().sorted()
                    _uiState.update {
                        it.copy(channels = channels, groups = groups, isLoading = false, error = null)
                    }
                    loadNowNext(channels.mapNotNull { it.epgChannelId })
                }
        }
    }

    private fun loadNowNext(epgChannelIds: List<String>) {
        viewModelScope.launch {
            val nowMs = System.currentTimeMillis()
            val nowPlaying = mutableMapOf<String, com.rooftop.domain.model.Programme?>()
            val nextUp = mutableMapOf<String, com.rooftop.domain.model.Programme?>()
            epgChannelIds.forEach { id ->
                nowPlaying[id] = epgRepository.getNow(id, nowMs)
                nextUp[id] = epgRepository.getNext(id, nowMs)
            }
            _uiState.update { it.copy(nowPlaying = nowPlaying, nextUp = nextUp) }
        }
    }

    fun onGroupSelected(group: String?) {
        _uiState.update { it.copy(selectedGroup = group) }
        observeChannels()
    }

    fun refresh(playlistId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = refreshChannelsUseCase(playlistId)
            if (result is Result.Error) {
                _uiState.update { it.copy(isLoading = false, error = result.exception.message) }
            }
        }
    }
}
