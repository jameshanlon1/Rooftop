package com.rooftop.ui.tv.channels

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.rooftop.domain.model.Channel

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ChannelListScreen(
    onChannelSelected: (Channel) -> Unit,
    viewModel: ChannelListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("Loading channels…", style = MaterialTheme.typography.bodyLarge)
                }
            }
            uiState.error != null -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = uiState.error ?: "Unknown error",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            uiState.channels.isEmpty() -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "No channels — add a playlist in settings to get started",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            else -> {
                Row(modifier = Modifier.fillMaxSize()) {
                    // Group sidebar
                    LazyColumn(
                        modifier = Modifier
                            .width(200.dp)
                            .fillMaxHeight(),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        item {
                            GroupItem(
                                name = "All",
                                isSelected = uiState.selectedGroup == null,
                                onClick = { viewModel.onGroupSelected(null) }
                            )
                        }
                        items(uiState.groups) { group ->
                            GroupItem(
                                name = group,
                                isSelected = uiState.selectedGroup == group,
                                onClick = { viewModel.onGroupSelected(group) }
                            )
                        }
                    }

                    // Channel grid
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 160.dp),
                        contentPadding = PaddingValues(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.channels) { channel ->
                            ChannelCard(
                                channel = channel,
                                onClick = { onChannelSelected(channel) },
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun GroupItem(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}
