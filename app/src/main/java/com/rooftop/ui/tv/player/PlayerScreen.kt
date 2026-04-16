package com.rooftop.ui.tv.player

import android.view.KeyEvent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.ui.PlayerView
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.rooftop.domain.model.Channel

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PlayerScreen(
    channelId: Long,
    onBack: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val lifecycleOwner = LocalLifecycleOwner.current

    BackHandler { onBack() }

    LaunchedEffect(channelId) { viewModel.loadChannel(channelId) }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> viewModel.player.pause()
                Lifecycle.Event.ON_RESUME -> if (uiState.isPlaying) viewModel.player.play()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .focusRequester(focusRequester)
            .focusable()
            .onPreviewKeyEvent { keyEvent ->
                if (keyEvent.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                when (keyEvent.nativeKeyEvent.keyCode) {
                    KeyEvent.KEYCODE_DPAD_UP,
                    KeyEvent.KEYCODE_CHANNEL_UP -> { viewModel.zapUp(); true }

                    KeyEvent.KEYCODE_DPAD_DOWN,
                    KeyEvent.KEYCODE_CHANNEL_DOWN -> { viewModel.zapDown(); true }

                    KeyEvent.KEYCODE_DPAD_CENTER,
                    KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> { viewModel.togglePlayPause(); true }

                    KeyEvent.KEYCODE_MEDIA_PLAY -> { viewModel.player.play(); true }
                    KeyEvent.KEYCODE_MEDIA_PAUSE -> { viewModel.player.pause(); true }

                    KeyEvent.KEYCODE_MENU,
                    KeyEvent.KEYCODE_INFO -> { viewModel.toggleTrackSelector(); true }

                    else -> { viewModel.showControls(); false }
                }
            }
    ) {
        // Video surface
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = viewModel.player
                    useController = false
                }
            },
            update = { it.player = viewModel.player },
            onRelease = { it.player = null },
            modifier = Modifier.fillMaxSize()
        )

        // Buffering spinner
        if (uiState.isBuffering) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Loading…",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        // Error
        if (uiState.error != null) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Playback error: ${uiState.error}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        // Controls overlay
        AnimatedVisibility(
            visible = uiState.isControlsVisible && !uiState.showTrackSelector,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ControlsOverlay(channel = uiState.currentChannel, isPlaying = uiState.isPlaying)
        }

        // Track selector
        if (uiState.showTrackSelector) {
            TrackSelectionSheet(
                audioTracks = uiState.audioTracks,
                subtitleTracks = uiState.subtitleTracks,
                onDismiss = { viewModel.toggleTrackSelector() },
                onAudioTrackSelected = { viewModel.selectAudioTrack(it) },
                onSubtitleTrackSelected = { viewModel.selectSubtitleTrack(it) }
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun ControlsOverlay(channel: Channel?, isPlaying: Boolean) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Top gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.75f), Color.Transparent)
                    )
                )
        )
        // Bottom gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                    )
                )
        )

        // Channel info — top left
        if (channel != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(32.dp)
            ) {
                if (!channel.logoUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = channel.logoUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(52.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Column {
                    Text(
                        text = channel.name,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                    if (!channel.groupTitle.isNullOrBlank()) {
                        Text(
                            text = channel.groupTitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        // Hints — bottom centre
        Text(
            text = "▲▼ Change channel  •  OK ${if (isPlaying) "Pause" else "Play"}  •  MENU Tracks",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.6f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }
}
