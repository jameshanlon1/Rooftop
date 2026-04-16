package com.rooftop.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rooftop.domain.model.WatchProgress
import com.rooftop.player.PlaybackRequest
import com.rooftop.player.PlaybackRequestHolder
import com.rooftop.ui.tv.channels.ChannelListScreen
import com.rooftop.ui.tv.epg.EpgScreen
import com.rooftop.ui.tv.player.PlayerScreen
import com.rooftop.ui.tv.series.SeriesDetailScreen
import com.rooftop.ui.tv.series.SeriesScreen
import com.rooftop.ui.tv.vod.VodDetailScreen
import com.rooftop.ui.tv.vod.VodPlayerScreen
import com.rooftop.ui.tv.vod.VodScreen

@Composable
fun RooftopNavGraph(playbackRequestHolder: PlaybackRequestHolder) {
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

        composable(Route.Vod.path) {
            VodScreen(
                onVodSelected = { vod ->
                    navController.navigate(Route.VodDetail.withArgs(vod.id))
                },
                onContinueWatching = { progress ->
                    playbackRequestHolder.request = PlaybackRequest(
                        contentId = progress.contentId,
                        streamUrl = progress.streamUrl,
                        title = progress.title,
                        posterUrl = progress.posterUrl,
                        savedPositionMs = progress.positionMs,
                        contentType = progress.contentType.name
                    )
                    navController.navigate(Route.VodPlayer.path)
                }
            )
        }

        composable(
            route = Route.VodDetail.path,
            arguments = listOf(navArgument("vodId") { type = NavType.LongType })
        ) { back ->
            val vodId = back.arguments?.getLong("vodId") ?: return@composable
            VodDetailScreen(
                vodId = vodId,
                onPlay = { navController.navigate(Route.VodPlayer.path) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Route.VodPlayer.path) {
            VodPlayerScreen(onBack = { navController.popBackStack() })
        }

        composable(Route.Series.path) {
            SeriesScreen(
                onSeriesSelected = { series ->
                    navController.navigate(Route.SeriesDetail.withArgs(series.id))
                }
            )
        }

        composable(
            route = Route.SeriesDetail.path,
            arguments = listOf(navArgument("seriesId") { type = NavType.LongType })
        ) { back ->
            val seriesId = back.arguments?.getLong("seriesId") ?: return@composable
            SeriesDetailScreen(
                seriesId = seriesId,
                onPlayEpisode = { navController.navigate(Route.VodPlayer.path) },
                onBack = { navController.popBackStack() }
            )
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
