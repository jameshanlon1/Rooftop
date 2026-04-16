package com.rooftop.ui.tv.vod

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.IconButton
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
                    // Full-screen backdrop
                    AsyncImage(
                        model = info.backdropUrl ?: info.item.posterUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Dark gradient from bottom (covers lower 60% of screen)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    0.0f to Color.Transparent,
                                    0.35f to Color.Black.copy(alpha = 0.5f),
                                    1.0f to Color.Black.copy(alpha = 0.95f)
                                )
                            )
                    )
                    // Left-side gradient for text readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    0.0f to Color.Black.copy(alpha = 0.6f),
                                    0.7f to Color.Transparent
                                )
                            )
                    )

                    // Content anchored to bottom
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .padding(start = 48.dp, end = 48.dp, bottom = 32.dp)
                    ) {
                        // Title
                        Text(
                            text = info.item.name,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 36.sp
                            ),
                            color = Color.White,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Rating / Year / Duration / FHD badges
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (!info.item.rating.isNullOrBlank()) {
                                RatingBadge(info.item.rating!!)
                            }
                            if (!info.item.year.isNullOrBlank()) {
                                MetaText(info.item.year!!)
                            }
                            QualityBadge("FHD")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Genre
                        if (!info.genre.isNullOrBlank()) {
                            Text(
                                text = info.genre!!,
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White.copy(alpha = 0.65f)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // Buttons row: Play, Favourite, More
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.preparePlayback()
                                    onPlay()
                                }
                            ) {
                                Text(
                                    text = if (uiState.savedPositionMs > 0) "▶  Resume" else "▶  Play",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }

                            IconButton(onClick = { viewModel.toggleFavourite() }) {
                                Icon(
                                    imageVector = if (uiState.isFavourite) Icons.Default.Favorite
                                                  else Icons.Default.FavoriteBorder,
                                    contentDescription = if (uiState.isFavourite) "Remove from favourites"
                                                         else "Add to favourites",
                                    tint = if (uiState.isFavourite) Color.Red else Color.White
                                )
                            }

                            IconButton(onClick = { /* more options */ }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "More",
                                    tint = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Synopsis
                        if (!info.plot.isNullOrBlank()) {
                            Text(
                                text = info.plot!!,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.80f),
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.fillMaxWidth(0.55f)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        // Cast / Director
                        if (!info.cast.isNullOrBlank()) {
                            Text(
                                text = "Cast: ${info.cast}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.55f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.fillMaxWidth(0.55f)
                            )
                        }

                        // Recommended row
                        if (uiState.recommendedVod.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Recommended",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            LazyRow(
                                contentPadding = PaddingValues(end = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(uiState.recommendedVod) { vod ->
                                    VodCard(
                                        vod = vod,
                                        onClick = { /* navigating from detail not needed — parent handles */ },
                                        modifier = Modifier.width(110.dp)
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

@Composable
private fun RatingBadge(rating: String) {
    Box(
        modifier = Modifier
            .background(Color(0xFFFFCC00), shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = "★ $rating",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = Color.Black
        )
    }
}

@Composable
private fun QualityBadge(label: String) {
    Box(
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
    }
}

@Composable
private fun MetaText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = Color.White.copy(alpha = 0.75f)
    )
}
