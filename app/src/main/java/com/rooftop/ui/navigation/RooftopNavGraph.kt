package com.rooftop.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rooftop.player.PlaybackRequest
import com.rooftop.player.PlaybackRequestHolder
import com.rooftop.ui.shared.TopNavBar
import com.rooftop.ui.shared.TopNavTab
import com.rooftop.ui.tv.channels.ChannelListScreen
import com.rooftop.ui.tv.epg.EpgScreen
import com.rooftop.ui.tv.home.HomeScreen
import com.rooftop.ui.tv.player.PlayerScreen
import com.rooftop.ui.tv.search.SearchScreen
import com.rooftop.ui.tv.series.SeriesDetailScreen
import com.rooftop.ui.tv.series.SeriesScreen
import com.rooftop.ui.tv.settings.SettingsScreen
import com.rooftop.ui.tv.vod.VodDetailScreen
import com.rooftop.ui.tv.vod.VodPlayerScreen
import com.rooftop.ui.tv.vod.VodScreen

private val TOP_LEVEL_ROUTES = setOf(
    Route.Home.path,
    Route.Search.path,
    Route.ChannelList.path,
    Route.Vod.path,
    Route.Series.path,
    Route.Settings.path
)

private fun routeToTab(route: String?): TopNavTab = when (route) {
    Route.Home.path -> TopNavTab.HOME
    Route.Search.path -> TopNavTab.SEARCH
    Route.ChannelList.path -> TopNavTab.LIVE_TV
    Route.Vod.path -> TopNavTab.MOVIES
    Route.Series.path -> TopNavTab.SERIES
    Route.Settings.path -> TopNavTab.SETTINGS
    else -> TopNavTab.HOME
}

@Composable
fun RooftopNavGraph(playbackRequestHolder: PlaybackRequestHolder) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showNavBar = currentRoute in TOP_LEVEL_ROUTES

    val navFocusRequester = remember { FocusRequester() }
    var navBarHasFocus by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        if (showNavBar) {
            TopNavBar(
                selectedTab = routeToTab(currentRoute),
                isHome = currentRoute == Route.Home.path,
                focusRequester = navFocusRequester,
                onFocusChanged = { navBarHasFocus = it },
                onTabSelected = { tab ->
                    val route = when (tab) {
                        TopNavTab.HOME -> Route.Home.path
                        TopNavTab.SEARCH -> Route.Search.path
                        TopNavTab.LIVE_TV -> Route.ChannelList.path
                        TopNavTab.MOVIES -> Route.Vod.path
                        TopNavTab.SERIES -> Route.Series.path
                        TopNavTab.SETTINGS -> Route.Settings.path
                    }
                    if (route != currentRoute) {
                        navController.navigate(route) {
                            popUpTo(Route.Home.path) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }

        // focusProperties routes D-pad Up (when exiting content area) to the selected nav tab
        Box(
            modifier = Modifier
                .weight(1f)
                .focusProperties { up = navFocusRequester }
        ) {
            NavHost(
                navController = navController,
                startDestination = Route.Home.path,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(Route.Home.path) {
                    // BackHandler inside composable block takes priority over NavHost's own handler
                    BackHandler(enabled = !navBarHasFocus) {
                        try { navFocusRequester.requestFocus() } catch (_: Exception) {}
                    }
                    HomeScreen(
                        onNavigateToSeries = { seriesId ->
                            navController.navigate(Route.SeriesDetail.withArgs(seriesId))
                        },
                        onNavigateToVod = { vodId ->
                            navController.navigate(Route.VodDetail.withArgs(vodId))
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
                        },
                        onNavigateToSettings = { navController.navigate(Route.Settings.path) }
                    )
                }

                composable(Route.Search.path) {
                    BackHandler(enabled = !navBarHasFocus) {
                        try { navFocusRequester.requestFocus() } catch (_: Exception) {}
                    }
                    SearchScreen(
                        onChannelSelected = { channel ->
                            navController.navigate(Route.Player.withArgs(channel.id))
                        },
                        onVodSelected = { vod ->
                            navController.navigate(Route.VodDetail.withArgs(vod.id))
                        },
                        onSeriesSelected = { series ->
                            navController.navigate(Route.SeriesDetail.withArgs(series.id))
                        }
                    )
                }

                composable(Route.ChannelList.path) {
                    BackHandler(enabled = !navBarHasFocus) {
                        try { navFocusRequester.requestFocus() } catch (_: Exception) {}
                    }
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
                    BackHandler(enabled = !navBarHasFocus) {
                        try { navFocusRequester.requestFocus() } catch (_: Exception) {}
                    }
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
                    BackHandler(enabled = !navBarHasFocus) {
                        try { navFocusRequester.requestFocus() } catch (_: Exception) {}
                    }
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

                composable(Route.Settings.path) {
                    BackHandler(enabled = !navBarHasFocus) {
                        try { navFocusRequester.requestFocus() } catch (_: Exception) {}
                    }
                    SettingsScreen()
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
    }
}
