package com.rooftop.ui.navigation

sealed class Route(val path: String) {
    object ChannelList : Route("channel_list")
    object Settings : Route("settings")
    object Epg : Route("epg")
    object Vod : Route("vod")
    object Series : Route("series")
    object Player : Route("player/{channelId}") {
        fun withArgs(channelId: Long) = "player/$channelId"
    }
}
