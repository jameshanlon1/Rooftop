package com.rooftop.ui.tv.home

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.OutlinedButton
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.rooftop.domain.model.Series
import com.rooftop.domain.model.VodItem
import com.rooftop.domain.model.WatchProgress
import com.rooftop.ui.shared.SectionHeader
import com.rooftop.ui.tv.series.SeriesCard
import com.rooftop.ui.tv.vod.VodCard
import kotlinx.coroutines.delay

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSeries: (Long) -> Unit,
    onNavigateToVod: (Long) -> Unit,
    onContinueWatching: (WatchProgress) -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        val hasContent = uiState.featuredItems.isNotEmpty() ||
            uiState.continueWatching.isNotEmpty() ||
            uiState.recentMovies.isNotEmpty() ||
            uiState.recentSeries.isNotEmpty()

        if (!uiState.isLoading && !hasContent) {
            // §2 empty state — no playlist added yet
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Welcome to Rooftop",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Add a playlist in Settings to get started",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = onNavigateToSettings) {
                        Text("Open Settings")
                    }
                }
            }
            return@Surface
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            // §2.1 Hero / Featured Banner with pager
            if (uiState.featuredItems.isNotEmpty()) {
                item {
                    HeroBannerPager(
                        items = uiState.featuredItems,
                        onSeriesSelected = onNavigateToSeries,
                        onVodSelected = onNavigateToVod
                    )
                }
            }

            // §2.2 Continue Watching
            if (uiState.continueWatching.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    SectionHeader("Continue Watching")
                    Spacer(modifier = Modifier.height(12.dp))
                    EdgeFadeRow {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 48.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.continueWatching) { progress ->
                                ContinueWatchingItem(
                                    progress = progress,
                                    onClick = { onContinueWatching(progress) }
                                )
                            }
                        }
                    }
                }
            }

            // §2.4 Recently Added Movies
            if (uiState.recentMovies.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    SectionHeader(
                        title = "Recently Added Movies",
                        onViewAll = { onNavigateToVod(uiState.recentMovies.first().id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    EdgeFadeRow {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 48.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.recentMovies) { vod ->
                                VodCard(
                                    vod = vod,
                                    onClick = { onNavigateToVod(vod.id) },
                                    modifier = Modifier.width(120.dp)
                                )
                            }
                        }
                    }
                }
            }

            // §2.5 Recently Added Series
            if (uiState.recentSeries.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    SectionHeader(
                        title = "Recently Added Series",
                        onViewAll = { onNavigateToSeries(uiState.recentSeries.first().id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    EdgeFadeRow {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 48.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.recentSeries) { series ->
                                SeriesCard(
                                    series = series,
                                    onClick = { onNavigateToSeries(series.id) },
                                    modifier = Modifier.width(120.dp)
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

// §2.1 — Rotating hero with auto-advance every 8s and pagination dots
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun HeroBannerPager(
    items: List<FeaturedItem>,
    onSeriesSelected: (Long) -> Unit,
    onVodSelected: (Long) -> Unit
) {
    val pagerState = rememberPagerState { items.size }
    val heroHeight = (LocalConfiguration.current.screenHeightDp * 0.55f).dp

    // Auto-advance every 8 seconds when idle
    LaunchedEffect(pagerState) {
        if (items.size <= 1) return@LaunchedEffect
        while (true) {
            delay(8_000L)
            if (!pagerState.isScrollInProgress) {
                pagerState.animateScrollToPage((pagerState.currentPage + 1) % items.size)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(heroHeight)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            HeroPage(
                item = items[page],
                onSeriesSelected = onSeriesSelected,
                onVodSelected = onVodSelected
            )
        }

        // Pagination dots — bottom centre (§2.1)
        if (items.size > 1) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {
                repeat(items.size) { i ->
                    val isActive = i == pagerState.currentPage
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(if (isActive) 24.dp else 8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (isActive) MaterialTheme.colorScheme.primary
                                else Color.White.copy(alpha = 0.4f)
                            )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun HeroPage(
    item: FeaturedItem,
    onSeriesSelected: (Long) -> Unit,
    onVodSelected: (Long) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (!item.backdropUrl.isNullOrBlank()) {
            AsyncImage(
                model = item.backdropUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant))
        }

        // Left-to-right gradient (§0.3 + §2.1)
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.horizontalGradient(
                    0.0f to Color.Black.copy(alpha = 0.85f),
                    0.55f to Color.Black.copy(alpha = 0.35f),
                    1.0f to Color.Transparent
                )
            )
        )
        // Bottom fade
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    0.55f to Color.Transparent,
                    1.0f to Color.Black.copy(alpha = 0.65f)
                )
            )
        )

        // Content — bottom left (§0.3)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 48.dp, bottom = 48.dp, end = 240.dp)
        ) {
            if (item.description.isNotBlank()) {
                Text(
                    text = item.description.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 2.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
            }
            Text(
                text = item.title,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {
                        when {
                            item.seriesId != null -> onSeriesSelected(item.seriesId)
                            item.vodId != null -> onVodSelected(item.vodId)
                        }
                    }
                ) {
                    Text(if (item.seriesId != null) "▶  Go to Series" else "▶  Play")
                }
                if (item.seriesId != null || item.vodId != null) {
                    OutlinedButton(
                        onClick = {
                            when {
                                item.seriesId != null -> onSeriesSelected(item.seriesId)
                                item.vodId != null -> onVodSelected(item.vodId)
                            }
                        }
                    ) {
                        Text("More Info")
                    }
                }
            }
        }
    }
}

// §2.2 — Continue Watching card (landscape 16:9, 260dp wide)
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun ContinueWatchingItem(progress: WatchProgress, onClick: () -> Unit) {
    Surface(onClick = onClick, modifier = Modifier.width(200.dp)) {
        Box {
            AsyncImage(
                model = progress.posterUrl,
                contentDescription = progress.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(200.dp)
                    .height(112.dp)  // 16:9
            )
            // Progress bar at bottom (§2.2 — 3dp, primary colour)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth(progress.progressFraction)
                    .height(3.dp)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

// §0.5 — Row edge fade masks using drawWithContent
@Composable
private fun EdgeFadeRow(content: @Composable () -> Unit) {
    val bgColor = MaterialTheme.colorScheme.background
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawWithContent {
                drawContent()
                // Fade left and right edges 5% each side
                drawRect(
                    brush = Brush.horizontalGradient(
                        0f to bgColor,
                        0.05f to Color.Transparent,
                        0.95f to Color.Transparent,
                        1f to bgColor
                    ),
                    size = size
                )
            }
    ) {
        content()
    }
}
