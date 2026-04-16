package com.rooftop.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rooftop.ui.tv.channels.ChannelListScreen
import com.rooftop.ui.tv.epg.EpgScreen
import com.rooftop.ui.tv.player.PlayerScreen

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
                    navController.navigate(Route.Player.withArgs(channel.id))
                },
                onOpenEpg = { navController.navigate(Route.Epg.path) }
            )
        }

        composable(Route.Epg.path) {
            EpgScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = Route.Player.path,
            arguments = listOf(navArgument("channelId") { type = NavType.LongType })
        ) { backStackEntry ->
            val channelId = backStackEntry.arguments?.getLong("channelId") ?: return@composable
            PlayerScreen(
                channelId = channelId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
