package com.rooftop.ui.tv.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TrackSelectionSheet(
    audioTracks: List<TrackInfo>,
    subtitleTracks: List<TrackInfo>,
    onDismiss: () -> Unit,
    onAudioTrackSelected: (TrackInfo) -> Unit,
    onSubtitleTrackSelected: (TrackInfo?) -> Unit
) {
    // Dim background — tap outside to dismiss
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable { onDismiss() }
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight(0.7f)
                .width(300.dp)
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .clickable { /* consume clicks so background dismissal doesn't fire */ }
        ) {
            // Audio tracks column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Audio",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                if (audioTracks.isEmpty()) {
                    Text("None", color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    LazyColumn {
                        items(audioTracks) { track ->
                            TrackItem(
                                label = track.label,
                                isSelected = track.isSelected,
                                onClick = { onAudioTrackSelected(track) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Subtitle tracks column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Subtitles",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                TrackItem(
                    label = "Off",
                    isSelected = subtitleTracks.none { it.isSelected },
                    onClick = { onSubtitleTrackSelected(null) }
                )
                LazyColumn {
                    items(subtitleTracks) { track ->
                        TrackItem(
                            label = track.label,
                            isSelected = track.isSelected,
                            onClick = { onSubtitleTrackSelected(track) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TrackItem(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Text(
        text = if (isSelected) "• $label" else "  $label",
        color = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp)
    )
}
