package com.rooftop.ui.tv.vod

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import com.rooftop.domain.model.ContentType
import com.rooftop.domain.model.WatchProgress
import com.rooftop.domain.repository.WatchProgressRepository
import com.rooftop.player.PlaybackRequestHolder
import com.rooftop.player.PlayerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VodPlayerUiState(
    val title: String = "",
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = true,
    val isControlsVisible: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class VodPlayerViewModel @Inject constructor(
    private val playerManager: PlayerManager,
    private val playbackRequestHolder: PlaybackRequestHolder,
    private val watchProgressRepository: WatchProgressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VodPlayerUiState())
    val uiState: StateFlow<VodPlayerUiState> = _uiState.asStateFlow()

    val player get() = playerManager.player

    private val request = playbackRequestHolder.request
    private var controlsJob: Job? = null

    private val listener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _uiState.update { it.copy(isPlaying = isPlaying) }
        }
        override fun onPlaybackStateChanged(state: Int) {
            _uiState.update { it.copy(isBuffering = state == Player.STATE_BUFFERING) }
        }
        override fun onPlayerError(error: PlaybackException) {
            _uiState.update { it.copy(error = error.message, isBuffering = false) }
        }
    }

    init {
        player.addListener(listener)
        request?.let { req ->
            _uiState.update { it.copy(title = req.title) }
            playerManager.play(req.streamUrl)
            if (req.savedPositionMs > 0) player.seekTo(req.savedPositionMs)
            scheduleHideControls()
        }
    }

    fun togglePlayPause() {
        if (player.isPlaying) player.pause() else player.play()
        showControls()
    }

    fun showControls() {
        _uiState.update { it.copy(isControlsVisible = true) }
        scheduleHideControls()
    }

    private fun scheduleHideControls() {
        controlsJob?.cancel()
        controlsJob = viewModelScope.launch {
            delay(4_000)
            _uiState.update { it.copy(isControlsVisible = false) }
        }
    }

    override fun onCleared() {
        saveProgress()
        player.removeListener(listener)
        playerManager.stop()
        super.onCleared()
    }

    private fun saveProgress() {
        val req = request ?: return
        val position = player.currentPosition
        if (position <= 2_000) return  // don't save if barely started
        val duration = player.duration.coerceAtLeast(0)
        viewModelScope.launch {
            watchProgressRepository.saveProgress(
                WatchProgress(
                    contentId = req.contentId,
                    title = req.title,
                    posterUrl = req.posterUrl,
                    streamUrl = req.streamUrl,
                    positionMs = position,
                    durationMs = duration,
                    contentType = if (req.contentType == "EPISODE") ContentType.EPISODE else ContentType.VOD
                )
            )
        }
    }
}
