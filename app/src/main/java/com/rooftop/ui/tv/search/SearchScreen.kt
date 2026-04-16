package com.rooftop.ui.tv.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.rooftop.domain.model.Channel
import com.rooftop.domain.model.Series
import com.rooftop.domain.model.VodItem
import com.rooftop.ui.tv.series.SeriesCard
import com.rooftop.ui.tv.vod.VodCard

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SearchScreen(
    onChannelSelected: (Channel) -> Unit,
    onVodSelected: (VodItem) -> Unit,
    onSeriesSelected: (Series) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            // Search input
            Surface(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
            ) {
                BasicTextField(
                    value = uiState.query,
                    onValueChange = { viewModel.onQueryChanged(it) },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    singleLine = true,
                    decorationBox = { inner ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                            if (uiState.query.isEmpty()) {
                                Text(
                                    "Search channels, movies, series…",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            inner()
                        }
                    }
                )
            }

            if (uiState.query.isBlank()) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        "Start typing to search",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                return@Column
            }

            val hasResults = uiState.channels.isNotEmpty() ||
                    uiState.vodItems.isNotEmpty() ||
                    uiState.seriesItems.isNotEmpty()

            if (!hasResults) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        "No results for \"${uiState.query}\"",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                return@Column
            }

            androidx.compose.foundation.lazy.LazyColumn(modifier = Modifier.fillMaxSize()) {
                if (uiState.channels.isNotEmpty()) {
                    item {
                        SectionHeader("Live Channels")
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(160.dp),
                            contentPadding = PaddingValues(bottom = 12.dp),
                            modifier = Modifier.height(
                                ((uiState.channels.size / 4 + 1) * 56).dp.coerceAtMost(200.dp)
                            )
                        ) {
                            gridItems(uiState.channels) { channel ->
                                Surface(
                                    onClick = { onChannelSelected(channel) },
                                    modifier = Modifier.padding(4.dp)
                                ) {
                                    Text(
                                        text = channel.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                if (uiState.vodItems.isNotEmpty()) {
                    item {
                        SectionHeader("Movies")
                        androidx.compose.foundation.lazy.LazyRow(
                            contentPadding = PaddingValues(bottom = 12.dp),
                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.vodItems) { vod ->
                                VodCard(
                                    vod = vod,
                                    onClick = { onVodSelected(vod) },
                                    modifier = Modifier.width(130.dp)
                                )
                            }
                        }
                    }
                }

                if (uiState.seriesItems.isNotEmpty()) {
                    item {
                        SectionHeader("Series")
                        androidx.compose.foundation.lazy.LazyRow(
                            contentPadding = PaddingValues(bottom = 12.dp),
                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.seriesItems) { series ->
                                SeriesCard(
                                    series = series,
                                    onClick = { onSeriesSelected(series) },
                                    modifier = Modifier.width(130.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    androidx.tv.material3.Text(
        text = title,
        style = androidx.tv.material3.MaterialTheme.typography.titleSmall,
        color = androidx.tv.material3.MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}
