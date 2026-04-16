package com.rooftop.ui.tv.settings

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
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.OutlinedButton
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.rooftop.domain.model.Playlist
import com.rooftop.domain.model.PlaylistType

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 48.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Playlists section
            item {
                SectionTitle("Playlists")
            }

            if (uiState.playlists.isEmpty()) {
                item {
                    Text(
                        "No playlists added yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(uiState.playlists) { playlist ->
                PlaylistRow(
                    playlist = playlist,
                    onDelete = { viewModel.deletePlaylist(playlist.id) }
                )
            }

            item {
                Button(onClick = { viewModel.showAddDialog() }) {
                    Text("+ Add Playlist")
                }
            }

            // EPG section
            item { SectionTitle("EPG (Electronic Programme Guide)") }

            item {
                Column {
                    Text(
                        "XMLTV URL",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    SettingsTextField(
                        value = uiState.epgUrl,
                        placeholder = "https://example.com/epg.xml or .xml.gz",
                        onValueChange = { viewModel.onEpgUrlChanged(it) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.saveEpgUrl() }) {
                        Text("Save EPG URL")
                    }
                }
            }

            // Sync section
            item { SectionTitle("Sync") }

            item {
                Column {
                    Button(
                        onClick = { viewModel.syncAll() },
                        enabled = !uiState.isSyncing
                    ) {
                        Text(if (uiState.isSyncing) "Syncing…" else "Sync All Content")
                    }
                    if (uiState.syncStatus != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            uiState.syncStatus!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }

    // Add playlist dialog
    if (uiState.showAddDialog) {
        AddPlaylistDialog(
            uiState = uiState,
            onTypeChanged = { viewModel.onAddTypeChanged(it) },
            onNameChanged = { viewModel.onAddNameChanged(it) },
            onUrlChanged = { viewModel.onAddUrlChanged(it) },
            onXtreamBaseChanged = { viewModel.onAddXtreamBaseChanged(it) },
            onXtreamUserChanged = { viewModel.onAddXtreamUserChanged(it) },
            onXtreamPassChanged = { viewModel.onAddXtreamPassChanged(it) },
            onAdd = { viewModel.addPlaylist() },
            onDismiss = { viewModel.hideAddDialog() }
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun PlaylistRow(playlist: Playlist, onDelete: () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(playlist.name, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = when (playlist.type) {
                        PlaylistType.M3U -> "M3U  •  ${playlist.url ?: ""}"
                        PlaylistType.XTREAM -> "Xtream  •  ${playlist.xtreamBaseUrl ?: ""}"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            OutlinedButton(onClick = onDelete) {
                Text("Remove")
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun AddPlaylistDialog(
    uiState: SettingsUiState,
    onTypeChanged: (PlaylistType) -> Unit,
    onNameChanged: (String) -> Unit,
    onUrlChanged: (String) -> Unit,
    onXtreamBaseChanged: (String) -> Unit,
    onXtreamUserChanged: (String) -> Unit,
    onXtreamPassChanged: (String) -> Unit,
    onAdd: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .width(480.dp)
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Add Playlist", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))

                // Type selector
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PlaylistType.values().forEach { type ->
                        Button(
                            onClick = { onTypeChanged(type) },
                        ) {
                            Text(
                                type.name,
                                color = if (uiState.addType == type)
                                    MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LabeledField("Name", uiState.addName, "My Playlist", onNameChanged)
                Spacer(modifier = Modifier.height(8.dp))

                when (uiState.addType) {
                    PlaylistType.M3U -> {
                        LabeledField("M3U URL", uiState.addUrl, "https://…/playlist.m3u", onUrlChanged)
                    }
                    PlaylistType.XTREAM -> {
                        LabeledField("Base URL", uiState.addXtreamBase, "http://provider.com:port", onXtreamBaseChanged)
                        Spacer(modifier = Modifier.height(8.dp))
                        LabeledField("Username", uiState.addXtreamUser, "username", onXtreamUserChanged)
                        Spacer(modifier = Modifier.height(8.dp))
                        LabeledField("Password", uiState.addXtreamPass, "password", onXtreamPassChanged)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onAdd) { Text("Add") }
                    OutlinedButton(onClick = onDismiss) { Text("Cancel") }
                }
            }
        }
    }
}

@Composable
private fun LabeledField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {
    Column {
        androidx.tv.material3.Text(
            label,
            style = androidx.tv.material3.MaterialTheme.typography.labelMedium,
            color = androidx.tv.material3.MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        SettingsTextField(value = value, placeholder = placeholder, onValueChange = onValueChange)
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun SettingsTextField(value: String, placeholder: String, onValueChange: (String) -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth()) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            decorationBox = { inner ->
                Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                    if (value.isEmpty()) {
                        Text(
                            placeholder,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    inner()
                }
            }
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    androidx.tv.material3.Text(
        text = title,
        style = androidx.tv.material3.MaterialTheme.typography.titleMedium,
        color = androidx.tv.material3.MaterialTheme.colorScheme.primary
    )
}
