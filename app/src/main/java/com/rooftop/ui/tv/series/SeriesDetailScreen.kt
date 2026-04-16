package com.rooftop.ui.tv.series

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.rooftop.domain.model.Episode

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SeriesDetailScreen(
    seriesId: Long,
    onPlayEpisode: () -> Unit,
    onBack: () -> Unit,
    viewModel: SeriesDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(seriesId) { viewModel.load(seriesId) }

    Surface(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("Loading…", style = MaterialTheme.typography.bodyLarge)
                }
            }
            uiState.detail == null -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("Series not found", style = MaterialTheme.typography.bodyLarge)
                }
            }
            else -> {
                val detail = uiState.detail!!
                val episodes = detail.seasons[uiState.selectedSeason] ?: emptyList()

                Box(modifier = Modifier.fillMaxSize()) {
                    // Backdrop
                    if (!detail.backdropUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = detail.backdropUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color.Black.copy(alpha = 0.9f), Color.Transparent)
                                    )
                                )
                        )
                    }

                    Row(modifier = Modifier.fillMaxSize().padding(48.dp)) {
                        // Cover + meta
                        Column(modifier = Modifier.width(220.dp)) {
                            AsyncImage(
                                model = detail.series.coverUrl,
                                contentDescription = detail.series.name,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.width(180.dp).height(260.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = detail.series.name,
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                            if (!detail.genre.isNullOrBlank()) {
                                Text(detail.genre!!, style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.7f))
                            }
                            if (!detail.series.rating.isNullOrBlank()) {
                                Text("★ ${detail.series.rating}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Yellow.copy(alpha = 0.9f))
                            }
                            if (!detail.plot.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = detail.plot!!,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.7f),
                                    maxLines = 5,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(32.dp))

                        // Season + episode list
                        Column(modifier = Modifier.weight(1f)) {
                            // Season tabs
                            if (detail.seasons.size > 1) {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(bottom = 16.dp)
                                ) {
                                    items(detail.seasons.keys.toList()) { season ->
                                        Button(
                                            onClick = { viewModel.selectSeason(season) }
                                        ) {
                                            Text(
                                                text = "Season $season",
                                                color = if (uiState.selectedSeason == season)
                                                    MaterialTheme.colorScheme.onPrimary
                                                else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }

                            // Episode list
                            LazyColumn(contentPadding = PaddingValues(vertical = 4.dp)) {
                                items(episodes, key = { it.id }) { episode ->
                                    EpisodeItem(
                                        episode = episode,
                                        onClick = {
                                            viewModel.prepareEpisodePlayback(episode)
                                            onPlayEpisode()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun EpisodeItem(episode: Episode, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Text(
                text = "${episode.episodeNum}.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.width(32.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = episode.title,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!episode.duration.isNullOrBlank()) {
                    Text(
                        text = episode.duration!!,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (!episode.plot.isNullOrBlank()) {
                    Text(
                        text = episode.plot!!,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Text("▶", style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
