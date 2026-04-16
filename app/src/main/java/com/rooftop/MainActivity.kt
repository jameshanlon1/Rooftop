package com.rooftop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.rooftop.player.PlaybackRequestHolder
import com.rooftop.ui.navigation.RooftopNavGraph
import com.rooftop.ui.theme.RooftopTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var playbackRequestHolder: PlaybackRequestHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RooftopTheme {
                RooftopNavGraph(playbackRequestHolder)
            }
        }
    }
}
