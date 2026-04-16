package com.rooftop.ui.tv.vod

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import coil.compose.AsyncImage

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun VodDetailScreen(
    vodId: Long,
    onPlay: () -> Unit,
    onBack: () -> Unit,
    viewModel: VodDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(vodId) { viewModel.load(vodId) }

    Surface(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("Loading…", style = MaterialTheme.typography.bodyLarge)
                }
            }
            uiState.vodInfo == null -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("Movie not found", style = MaterialTheme.typography.bodyLarge)
                }
            }
            else -> {
                val info = uiState.vodInfo!!
                Box(modifier = Modifier.fillMaxSize()) {
                    // Backdrop
                    if (!info.backdropUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = info.backdropUrl,
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

                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(48.dp)
                    ) {
                        // Poster
                        AsyncImage(
                            model = info.item.posterUrl,
                            contentDescription = info.item.name,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .width(180.dp)
                                .height(260.dp)
                        )

                        Spacer(modifier = Modifier.width(40.dp))

                        // Info
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = info.item.name,
                                style = MaterialTheme.typography.headlineLarge,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                if (!info.item.year.isNullOrBlank()) {
                                    Text(info.item.year!!, color = Color.White.copy(alpha = 0.7f),
                                        style = MaterialTheme.typography.bodyMedium)
                                }
                                if (!info.genre.isNullOrBlank()) {
                                    Text(info.genre!!, color = Color.White.copy(alpha = 0.7f),
                                        style = MaterialTheme.typography.bodyMedium)
                                }
                                if (!info.item.rating.isNullOrBlank()) {
                                    Text("★ ${info.item.rating}", color = Color.Yellow.copy(alpha = 0.9f),
                                        style = MaterialTheme.typography.bodyMedium)
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            if (!info.plot.isNullOrBlank()) {
                                Text(
                                    text = info.plot!!,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            if (!info.cast.isNullOrBlank()) {
                                Text("Cast: ${info.cast}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.6f))
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            if (!info.director.isNullOrBlank()) {
                                Text("Director: ${info.director}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.6f))
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Button(onClick = {
                                    viewModel.preparePlayback()
                                    onPlay()
                                }) {
                                    Text(
                                        if (uiState.savedPositionMs > 0) "▶ Resume" else "▶ Play"
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
