package com.rooftop.ui.tv.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rooftop.data.local.dao.ChannelDao
import com.rooftop.data.local.dao.SeriesDao
import com.rooftop.data.local.dao.VodDao
import com.rooftop.data.local.mapper.toDomain
import com.rooftop.domain.model.Channel
import com.rooftop.domain.model.Series
import com.rooftop.domain.model.VodItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val channels: List<Channel> = emptyList(),
    val vodItems: List<VodItem> = emptyList(),
    val seriesItems: List<Series> = emptyList()
)

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val channelDao: ChannelDao,
    private val vodDao: VodDao,
    private val seriesDao: SeriesDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val queryFlow = MutableStateFlow("")

    init {
        viewModelScope.launch {
            queryFlow.debounce(300).collect { query ->
                if (query.length < 2) {
                    _uiState.update { it.copy(channels = emptyList(), vodItems = emptyList(), seriesItems = emptyList()) }
                    return@collect
                }
                val channels = channelDao.search(query).map { it.toDomain() }
                val vod = vodDao.search(query).map { it.toDomain() }
                val series = seriesDao.search(query).map { it.toDomain() }
                _uiState.update { it.copy(channels = channels, vodItems = vod, seriesItems = series) }
            }
        }
    }

    fun onQueryChanged(query: String) {
        _uiState.update { it.copy(query = query) }
        queryFlow.value = query
    }
}
