package com.rooftop.ui.tv.series

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rooftop.domain.repository.SeriesRepository
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
class SeriesViewModel @Inject constructor(
    private val seriesRepository: SeriesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SeriesUiState())
    val uiState: StateFlow<SeriesUiState> = _uiState.asStateFlow()

    init {
        observe()
    }

    private fun observe() {
        viewModelScope.launch {
            combine(
                seriesRepository.getSeriesItems(),
                seriesRepository.getCategories()
            ) { items, categories -> items to categories }
            .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
            .collect { (items, categories) ->
                _uiState.update {
                    it.copy(
                        items = if (it.selectedCategory != null)
                            items.filter { s -> s.category == it.selectedCategory }
                        else items,
                        categories = categories,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onCategorySelected(category: String?) {
        _uiState.update { it.copy(selectedCategory = category) }
        viewModelScope.launch {
            val flow = if (category != null) seriesRepository.getSeriesByCategory(category)
                       else seriesRepository.getSeriesItems()
            flow.collect { list -> _uiState.update { it.copy(items = list) } }
        }
    }
}
