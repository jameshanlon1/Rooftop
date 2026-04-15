package com.rooftop.ui.navigation

sealed class Route(val path: String) {
    object ChannelList : Route("channel_list")
    object Settings : Route("settings")
    object Epg : Route("epg")
    object Vod : Route("vod")
    object Series : Route("series")
    object Player : Route("player/{streamUrl}") {
        fun withArgs(streamUrl: String) =
            "player/${java.net.URLEncoder.encode(streamUrl, "UTF-8")}"
    }
}
