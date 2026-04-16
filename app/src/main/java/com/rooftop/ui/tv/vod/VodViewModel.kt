package com.rooftop.ui.tv.vod

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rooftop.domain.repository.VodRepository
import com.rooftop.domain.repository.WatchProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VodViewModel @Inject constructor(
    private val vodRepository: VodRepository,
    private val watchProgressRepository: WatchProgressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VodUiState())
    val uiState: StateFlow<VodUiState> = _uiState.asStateFlow()

    init {
        observe()
    }

    private fun observe() {
        viewModelScope.launch {
            combine(
                vodRepository.getVodItems(),
                vodRepository.getCategories(),
                watchProgressRepository.getRecentProgress()
            ) { items, categories, progress ->
                Triple(items, categories, progress)
            }
            .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
            .collect { (items, categories, progress) ->
                _uiState.update {
                    it.copy(
                        items = if (it.selectedCategory != null)
                            items.filter { v -> v.category == it.selectedCategory }
                        else items,
                        categories = categories,
                        continueWatching = progress.filter { p -> p.contentType.name == "VOD" },
                        isLoading = false,
                        error = null
                    )
                }
            }
        }
    }

    fun onCategorySelected(category: String?) {
        _uiState.update { it.copy(selectedCategory = category) }
        viewModelScope.launch {
            val items = if (category != null)
                vodRepository.getVodByCategory(category)
            else vodRepository.getVodItems()
            items.collect { list ->
                _uiState.update { it.copy(items = list) }
            }
        }
    }
}
