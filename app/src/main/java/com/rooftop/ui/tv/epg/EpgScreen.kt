package com.rooftop.ui.tv.epg

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.rooftop.domain.model.Channel
import com.rooftop.domain.model.Programme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

private val CHANNEL_COL_WIDTH = 120.dp
private val ROW_HEIGHT = 64.dp
private val TIME_HEADER_HEIGHT = 32.dp
private val PIXELS_PER_MINUTE = 4.dp   // 1 hour = 240dp

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun EpgScreen(
    onBack: () -> Unit,
    viewModel: EpgViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val channelListState = rememberLazyListState()
    val programmeListState = rememberLazyListState()
    val horizontalScroll = rememberScrollState()

    // Sync vertical scroll between channel column and programme rows
    LaunchedEffect(channelListState) {
        snapshotFlow { channelListState.firstVisibleItemIndex to channelListState.firstVisibleItemScrollOffset }
            .collect { (index, offset) ->
                if (!programmeListState.isScrollInProgress) {
                    programmeListState.scrollToItem(index, offset)
                }
            }
    }
    LaunchedEffect(programmeListState) {
        snapshotFlow { programmeListState.firstVisibleItemIndex to programmeListState.firstVisibleItemScrollOffset }
            .collect { (index, offset) ->
                if (!channelListState.isScrollInProgress) {
                    channelListState.scrollToItem(index, offset)
                }
            }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("Loading EPG…", style = MaterialTheme.typography.bodyLarge)
                }
            }
            uiState.channels.isEmpty() -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        "No channels — add a playlist first",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            else -> {
                EpgGrid(
                    channels = uiState.channels,
                    programmes = uiState.programmes,
                    nowMs = uiState.nowMs,
                    selectedProgramme = uiState.selectedProgramme,
                    channelListState = channelListState,
                    programmeListState = programmeListState,
                    horizontalScroll = horizontalScroll,
                    onProgrammeSelected = { viewModel.selectProgramme(it) }
                )
            }
        }

        // Programme detail panel
        uiState.selectedProgramme?.let { prog ->
            ProgrammeDetailPanel(
                programme = prog,
                onDismiss = { viewModel.selectProgramme(null) }
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun EpgGrid(
    channels: List<Channel>,
    programmes: Map<String, List<Programme>>,
    nowMs: Long,
    selectedProgramme: Programme?,
    channelListState: LazyListState,
    programmeListState: LazyListState,
    horizontalScroll: ScrollState,
    onProgrammeSelected: (Programme?) -> Unit
) {
    // Grid starts at start of current day
    val dayStartMs = run {
        val cal = java.util.Calendar.getInstance()
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        cal.timeInMillis
    }
    val totalMinutes = 24 * 60 * 7  // 7 days
    val gridWidth = PIXELS_PER_MINUTE * totalMinutes

    Column(modifier = Modifier.fillMaxSize()) {
        // Time header row
        Row(modifier = Modifier.fillMaxWidth().height(TIME_HEADER_HEIGHT)) {
            // Blank corner above channel column
            Box(modifier = Modifier.width(CHANNEL_COL_WIDTH).height(TIME_HEADER_HEIGHT)
                .background(MaterialTheme.colorScheme.surface))
            // Time labels
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(TIME_HEADER_HEIGHT)
                    .horizontalScroll(horizontalScroll, enabled = false)
            ) {
                Row(modifier = Modifier.width(gridWidth)) {
                    for (hour in 0 until totalMinutes / 60) {
                        Box(modifier = Modifier.width(PIXELS_PER_MINUTE * 60)) {
                            Text(
                                text = formatHour(dayStartMs + hour * 3600_000L),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Channel column + programme grid
        Row(modifier = Modifier.weight(1f)) {
            // Fixed channel name column
            LazyColumn(
                state = channelListState,
                modifier = Modifier.width(CHANNEL_COL_WIDTH)
            ) {
                items(channels, key = { it.id }) { channel ->
                    ChannelNameCell(channel = channel)
                }
            }

            // Scrollable programme rows
            LazyColumn(
                state = programmeListState,
                modifier = Modifier.weight(1f).horizontalScroll(horizontalScroll)
            ) {
                items(channels, key = { it.id }) { channel ->
                    val channelProgs = channel.epgChannelId?.let { programmes[it] } ?: emptyList()
                    ProgrammeRow(
                        programmes = channelProgs,
                        dayStartMs = dayStartMs,
                        nowMs = nowMs,
                        gridWidth = gridWidth,
                        selectedProgramme = selectedProgramme,
                        onProgrammeSelected = onProgrammeSelected
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun ChannelNameCell(channel: Channel) {
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier
            .width(CHANNEL_COL_WIDTH)
            .height(ROW_HEIGHT)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = channel.name,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun ProgrammeRow(
    programmes: List<Programme>,
    dayStartMs: Long,
    nowMs: Long,
    gridWidth: androidx.compose.ui.unit.Dp,
    selectedProgramme: Programme?,
    onProgrammeSelected: (Programme?) -> Unit
) {
    Box(
        modifier = Modifier
            .width(gridWidth)
            .height(ROW_HEIGHT)
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Now indicator line
        val nowOffsetMin = ((nowMs - dayStartMs) / 60_000f).coerceAtLeast(0f)
        val nowOffsetDp = PIXELS_PER_MINUTE * nowOffsetMin
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(ROW_HEIGHT)
                .padding(start = nowOffsetDp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
        )

        // Programme blocks
        programmes.forEach { prog ->
            val startMin = ((prog.start - dayStartMs) / 60_000f).coerceAtLeast(0f)
            val durationMin = ((prog.stop - prog.start) / 60_000f).coerceAtLeast(1f)
            val startDp = PIXELS_PER_MINUTE * startMin
            val widthDp = (PIXELS_PER_MINUTE * durationMin).coerceAtLeast(8.dp)
            val isSelected = prog.id == selectedProgramme?.id
            val isNow = prog.start <= nowMs && prog.stop > nowMs

            Surface(
                onClick = {
                    onProgrammeSelected(if (isSelected) null else prog)
                },
                modifier = Modifier
                    .padding(start = startDp, top = 2.dp, bottom = 2.dp)
                    .width(widthDp - 2.dp)
                    .height(ROW_HEIGHT - 4.dp)
                    .focusable()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            when {
                                isSelected -> MaterialTheme.colorScheme.primary
                                isNow -> MaterialTheme.colorScheme.primaryContainer
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = prog.title,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun ProgrammeDetailPanel(
    programme: Programme,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
    ) {
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(text = programme.title, style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${formatTime(programme.start)} – ${formatTime(programme.stop)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                if (!programme.description.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = programme.description,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Press BACK to dismiss",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
private val hourFormat = SimpleDateFormat("EEE HH:mm", Locale.getDefault())

private fun formatTime(ms: Long) = timeFormat.format(Date(ms))
private fun formatHour(ms: Long) = hourFormat.format(Date(ms))
