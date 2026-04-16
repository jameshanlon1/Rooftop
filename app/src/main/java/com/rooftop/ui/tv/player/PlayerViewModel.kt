package com.rooftop.ui.tv.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.Tracks
import com.rooftop.domain.repository.ChannelRepository
import com.rooftop.player.PlayerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playerManager: PlayerManager,
    private val channelRepository: ChannelRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    val player get() = playerManager.player

    private var controlsHideJob: Job? = null

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _uiState.update { it.copy(isPlaying = isPlaying) }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            _uiState.update { it.copy(isBuffering = playbackState == Player.STATE_BUFFERING) }
        }

        override fun onTracksChanged(tracks: Tracks) {
            updateAvailableTracks(tracks)
        }

        override fun onPlayerError(error: PlaybackException) {
            _uiState.update { it.copy(error = error.message, isBuffering = false) }
        }
    }

    init {
        player.addListener(playerListener)
    }

    fun loadChannel(channelId: Long) {
        viewModelScope.launch {
            val channels = channelRepository.getChannels().first()
            val index = channels.indexOfFirst { it.id == channelId }
            if (index == -1) return@launch
            _uiState.update {
                it.copy(channels = channels, currentIndex = index, currentChannel = channels[index])
            }
            playerManager.play(channels[index].streamUrl)
            scheduleHideControls()
        }
    }

    fun zapUp() = zapToIndex((_uiState.value.currentIndex + 1) % _uiState.value.channels.size)

    fun zapDown() {
        val current = _uiState.value.currentIndex
        val size = _uiState.value.channels.size
        zapToIndex(if (current == 0) size - 1 else current - 1)
    }

    private fun zapToIndex(index: Int) {
        if (_uiState.value.channels.isEmpty()) return
        val channel = _uiState.value.channels[index]
        _uiState.update {
            it.copy(currentIndex = index, currentChannel = channel, error = null, isBuffering = true)
        }
        playerManager.play(channel.streamUrl)
        showControls()
    }

    fun togglePlayPause() {
        if (player.isPlaying) player.pause() else player.play()
        showControls()
    }

    fun showControls() {
        _uiState.update { it.copy(isControlsVisible = true) }
        scheduleHideControls()
    }

    fun toggleTrackSelector() {
        _uiState.update { it.copy(showTrackSelector = !it.showTrackSelector) }
        controlsHideJob?.cancel()
    }

    fun selectAudioTrack(track: TrackInfo) {
        val trackGroups = player.currentTracks.groups
            .filter { it.type == C.TRACK_TYPE_AUDIO }
        if (track.groupIndex < trackGroups.size) {
            player.trackSelectionParameters = player.trackSelectionParameters
                .buildUpon()
                .setOverrideForType(
                    TrackSelectionOverride(trackGroups[track.groupIndex].mediaTrackGroup, track.trackIndex)
                )
                .build()
        }
        _uiState.update { it.copy(showTrackSelector = false) }
    }

    fun selectSubtitleTrack(track: TrackInfo?) {
        val params = player.trackSelectionParameters.buildUpon()
        if (track == null) {
            params.setIgnoredTextSelectionFlags(C.SELECTION_FLAG_DEFAULT)
        } else {
            val trackGroups = player.currentTracks.groups
                .filter { it.type == C.TRACK_TYPE_TEXT }
            if (track.groupIndex < trackGroups.size) {
                params.setOverrideForType(
                    TrackSelectionOverride(trackGroups[track.groupIndex].mediaTrackGroup, track.trackIndex)
                )
            }
        }
        player.trackSelectionParameters = params.build()
        _uiState.update { it.copy(showTrackSelector = false) }
    }

    private fun scheduleHideControls() {
        controlsHideJob?.cancel()
        controlsHideJob = viewModelScope.launch {
            delay(4_000)
            _uiState.update { it.copy(isControlsVisible = false) }
        }
    }

    private fun updateAvailableTracks(tracks: Tracks) {
        val audio = tracks.groups
            .filter { it.type == C.TRACK_TYPE_AUDIO }
            .flatMapIndexed { groupIndex, group ->
                (0 until group.length).map { trackIndex ->
                    val format = group.getTrackFormat(trackIndex)
                    TrackInfo(
                        groupIndex = groupIndex,
                        trackIndex = trackIndex,
                        label = format.language ?: format.label ?: "Audio ${groupIndex + 1}",
                        isSelected = group.isTrackSelected(trackIndex)
                    )
                }
            }
        val subtitles = tracks.groups
            .filter { it.type == C.TRACK_TYPE_TEXT }
            .flatMapIndexed { groupIndex, group ->
                (0 until group.length).map { trackIndex ->
                    val format = group.getTrackFormat(trackIndex)
                    TrackInfo(
                        groupIndex = groupIndex,
                        trackIndex = trackIndex,
                        label = format.language ?: format.label ?: "Subtitle ${groupIndex + 1}",
                        isSelected = group.isTrackSelected(trackIndex)
                    )
                }
            }
        _uiState.update { it.copy(audioTracks = audio, subtitleTracks = subtitles) }
    }

    override fun onCleared() {
        super.onCleared()
        player.removeListener(playerListener)
        playerManager.stop()
    }
}
