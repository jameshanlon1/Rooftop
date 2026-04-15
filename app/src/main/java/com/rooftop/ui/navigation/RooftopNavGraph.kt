package com.rooftop.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rooftop.ui.tv.channels.ChannelListScreen

@Composable
fun RooftopNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.ChannelList.path
    ) {
        composable(Route.ChannelList.path) {
            ChannelListScreen(
                onChannelSelected = { channel ->
                    navController.navigate(Route.Player.withArgs(channel.streamUrl))
                }
            )
        }
        // Phase 2+: player, EPG, VOD, Series, Settings
    }
}
