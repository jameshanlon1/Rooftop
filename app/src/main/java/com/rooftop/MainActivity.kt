package com.rooftop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.rooftop.ui.navigation.RooftopNavGraph
import com.rooftop.ui.theme.RooftopTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RooftopTheme {
                RooftopNavGraph()
            }
        }
    }
}
