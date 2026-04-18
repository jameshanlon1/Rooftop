package com.rooftop.ui.tv.settings

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Sync
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Button
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.OutlinedButton
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.rooftop.domain.model.Playlist
import com.rooftop.domain.model.PlaylistType
import com.rooftop.ui.theme.RooftopDivider
import com.rooftop.ui.theme.RooftopSecondary
import com.rooftop.ui.theme.RooftopSurface
import com.rooftop.ui.theme.RooftopSurfaceRaised

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 48.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            item {
                ActivePlaylistRow(
                    playlist = uiState.activePlaylist,
                    onAddPlaylist = { viewModel.showAddDialog() }
                )
            }

            item { Spacer(Modifier.height(16.dp)) }

            // ── PLAYLIST SETTINGS ─────────────────────────────────────────────
            item { GroupHeader("Playlist Settings") }
            item {
                SettingsCard {
                    SettingsRow(
                        label = "Refresh Content",
                        trailingContent = {
                            if (uiState.isSyncing) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Sync, null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Syncing…", style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary)
                                }
                            } else {
                                StatusBadge(
                                    label = uiState.syncStatus ?: "Synced",
                                    color = if (uiState.syncStatus?.contains("error") == true)
                                        RooftopSecondary else Color(0xFF22C55E)
                                )
                            }
                        },
                        onClick = { if (!uiState.isSyncing) viewModel.syncAll() }
                    )
                    SettingsDivider()
                    SettingsRow(
                        label = if (uiState.activePlaylist != null) "Edit Playlist" else "Add Playlist",
                        value = uiState.activePlaylist?.let {
                            when (it.type) {
                                PlaylistType.XTREAM -> "Xtream  •  ${it.xtreamBaseUrl ?: ""}"
                                PlaylistType.M3U -> "M3U  •  ${it.url ?: ""}"
                            }
                        },
                        showChevron = true,
                        onClick = { viewModel.showAddDialog() }
                    )
                    SettingsDivider()
                    SettingsRow(
                        label = "EPG Source URL",
                        value = uiState.epgUrl.ifBlank { "Not set" },
                        showChevron = true,
                        onClick = {}
                    )
                    SettingsDivider()
                    SettingsRow(label = "Manage Categories", value = "Coming soon",
                        showChevron = true, onClick = {})
                    SettingsDivider()
                    SettingsRow(label = "Information", showChevron = true, onClick = {})
                }
            }

            if (uiState.playlists.isNotEmpty()) {
                item { Spacer(Modifier.height(8.dp)) }
                item { GroupHeader("All Playlists") }
                items(uiState.playlists) { playlist ->
                    PlaylistCard(
                        playlist = playlist,
                        onDelete = { viewModel.deletePlaylist(playlist.id) }
                    )
                }
                item {
                    Surface(
                        onClick = { viewModel.showAddDialog() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ClickableSurfaceDefaults.colors(
                            containerColor = Color.Transparent,
                            focusedContainerColor = RooftopSurfaceRaised
                        )
                    ) {
                        Text(
                            "+ Add Playlist",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }

            // ── APP SETTINGS ──────────────────────────────────────────────────
            item { GroupHeader("App Settings") }
            item {
                SettingsCard {
                    SettingsRow(label = "Player", showChevron = true, onClick = {})
                    SettingsDivider()
                    SettingsToggleRow(label = "Auto-Play Next Episode",
                        checked = uiState.autoPlayNextEpisode,
                        onToggle = { viewModel.onAutoPlayNextEpisodeToggled() })
                    SettingsDivider()
                    SettingsRow(label = "Interface", showChevron = true, onClick = {})
                    SettingsDivider()
                    SettingsToggleRow(label = "Blur Unseen Episodes",
                        checked = uiState.blurUnseenEpisodes,
                        onToggle = { viewModel.onBlurUnseenEpisodesToggled() })
                    SettingsDivider()
                    SettingsToggleRow(label = "New Episode Alerts",
                        checked = uiState.newEpisodeAlerts,
                        onToggle = { viewModel.onNewEpisodeAlertsToggled() })
                    SettingsDivider()
                    SettingsToggleRow(label = "Show What's New on Update",
                        checked = uiState.showWhatsNewOnUpdate,
                        onToggle = { viewModel.onShowWhatsNewToggled() })
                    SettingsDivider()
                    SettingsRow(label = "Trakt", value = "Not connected",
                        showChevron = true, onClick = {})
                    SettingsDivider()
                    SettingsRow(label = "OpenSubtitles", value = "Not connected",
                        showChevron = true, onClick = {})
                }
            }

            item { Spacer(Modifier.height(16.dp)) }

            // ── STORAGE ───────────────────────────────────────────────────────
            item { GroupHeader("Storage") }
            item {
                SettingsCard {
                    SettingsRow(label = "Downloads", value = "Coming soon",
                        showChevron = true, onClick = {})
                    SettingsDivider()
                    SettingsRow(label = "Clear Cache", showChevron = false, onClick = {})
                }
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }

    if (uiState.showAddDialog) {
        Dialog(
            onDismissRequest = { viewModel.hideAddDialog() },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
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
}

// ─── Section header ───────────────────────────────────────────────────────────
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun GroupHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.2.sp
        ),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
    )
}

// ─── Card container ───────────────────────────────────────────────────────────
@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(RooftopSurface)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            content()
        }
    }
}

// ─── Navigation/info row — transparent container, subtle focus highlight ─────
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun SettingsRow(
    label: String,
    value: String? = null,
    showChevron: Boolean = false,
    trailingContent: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = Color.Transparent,
            focusedContainerColor = Color.White.copy(alpha = 0.07f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (trailingContent != null) {
                    trailingContent()
                } else if (!value.isNullOrBlank()) {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (showChevron) {
                    Icon(Icons.Default.ChevronRight, null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

// ─── Toggle row ──────────────────────────────────────────────────────────────
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun SettingsToggleRow(label: String, checked: Boolean, onToggle: () -> Unit) {
    Surface(
        onClick = onToggle,
        modifier = Modifier.fillMaxWidth(),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = Color.Transparent,
            focusedContainerColor = Color.White.copy(alpha = 0.07f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
            ToggleIndicator(checked = checked)
        }
    }
}

@Composable
private fun ToggleIndicator(checked: Boolean) {
    val trackColor = if (checked) MaterialTheme.colorScheme.primary
                     else Color.White.copy(alpha = 0.20f)
    Box(
        modifier = Modifier
            .width(44.dp)
            .height(24.dp)
            .clip(RoundedCornerShape(50))
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .padding(2.dp)
                .size(20.dp)
                .align(if (checked) Alignment.CenterEnd else Alignment.CenterStart)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}

// ─── Divider ─────────────────────────────────────────────────────────────────
@Composable
private fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp)
            .height(1.dp)
            .background(RooftopDivider)
    )
}

// ─── Status badge ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun StatusBadge(label: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = color
        )
    }
}

// ─── Active playlist header row ──────────────────────────────────────────────
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun ActivePlaylistRow(playlist: Playlist?, onAddPlaylist: () -> Unit) {
    Surface(
        onClick = if (playlist != null) ({}) else onAddPlaylist,
        modifier = Modifier.fillMaxWidth(),
        shape = ClickableSurfaceDefaults.shape(shape = RoundedCornerShape(16.dp)),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = RooftopSurface,
            focusedContainerColor = RooftopSurfaceRaised
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.PlayCircle, null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = playlist?.name ?: "No playlist added",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = if (playlist != null) "Active playlist  •  Tap to switch"
                           else "Add a playlist to get started",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (playlist == null) {
                Button(onClick = onAddPlaylist) { Text("Add Playlist") }
            } else {
                StatusBadge("Synced", Color(0xFF22C55E))
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Default.ChevronRight, null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp))
            }
        }
    }
}

// ─── Playlist list item ───────────────────────────────────────────────────────
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun PlaylistCard(playlist: Playlist, onDelete: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(RooftopSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(playlist.name, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = when (playlist.type) {
                        PlaylistType.M3U -> "M3U  •  ${playlist.url ?: ""}"
                        PlaylistType.XTREAM -> "Xtream  •  ${playlist.xtreamBaseUrl ?: ""}"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            OutlinedButton(onClick = onDelete) { Text("Remove") }
        }
    }
}

// ─── Add Playlist dialog ──────────────────────────────────────────────────────
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
            .background(Color.Black.copy(alpha = 0.70f)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(560.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(RooftopSurface)
        ) {
            Column(modifier = Modifier.padding(32.dp)) {

                Text("Add Playlist",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                Text("Add your content source",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(Modifier.height(24.dp))

                // ── TYPE ─────────────────────────────────────────────────────
                GroupHeader("Type")
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PlaylistType.values().forEach { type ->
                        val isSelected = uiState.addType == type
                        // Use ClickableSurfaceDefaults.colors so container color is correct — no white corners
                        Surface(
                            onClick = { onTypeChanged(type) },
                            shape = ClickableSurfaceDefaults.shape(shape = RoundedCornerShape(50)),
                            colors = ClickableSurfaceDefaults.colors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary
                                                 else Color.White.copy(alpha = 0.09f),
                                focusedContainerColor = if (isSelected) MaterialTheme.colorScheme.primary
                                                        else Color.White.copy(alpha = 0.16f)
                            )
                        ) {
                            Text(
                                text = when (type) {
                                    PlaylistType.M3U -> "M3U Playlist"
                                    PlaylistType.XTREAM -> "Xtream Codes"
                                },
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) Color.White
                                        else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // ── NAME ─────────────────────────────────────────────────────
                GroupHeader("Name")
                Spacer(Modifier.height(8.dp))
                InputField(
                    value = uiState.addName,
                    placeholder = "My Playlist",
                    onValueChange = onNameChanged
                )

                Spacer(Modifier.height(20.dp))

                // ── CONNECTION ───────────────────────────────────────────────
                GroupHeader("Connection")
                Spacer(Modifier.height(8.dp))
                when (uiState.addType) {
                    PlaylistType.XTREAM -> {
                        InputField(uiState.addXtreamBase, "Server  (http://example.com:8080)", onXtreamBaseChanged)
                        Spacer(Modifier.height(8.dp))
                        InputField(uiState.addXtreamUser, "Username", onXtreamUserChanged)
                        Spacer(Modifier.height(8.dp))
                        InputField(uiState.addXtreamPass, "Password", onXtreamPassChanged)
                    }
                    PlaylistType.M3U -> {
                        InputField(uiState.addUrl, "URL  (https://…/playlist.m3u)", onUrlChanged)
                    }
                }

                Spacer(Modifier.height(28.dp))

                // ── ACTIONS ──────────────────────────────────────────────────
                Button(
                    onClick = onAdd,
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Text("Connect",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold))
                }
                Spacer(Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

// ─── Plain text input field ───────────────────────────────────────────────────
@Composable
private fun InputField(value: String, placeholder: String, onValueChange: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.07f))
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            decorationBox = { inner ->
                Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 11.dp)) {
                    if (value.isEmpty()) {
                        Text(placeholder,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    inner()
                }
            }
        )
    }
}
