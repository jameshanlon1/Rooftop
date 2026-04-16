package com.rooftop.ui.navigation

sealed class Route(val path: String) {
    object Home : Route("home")
    object Search : Route("search")
    object ChannelList : Route("channel_list")
    object Settings : Route("settings")
    object Epg : Route("epg")
    object Vod : Route("vod")
    object VodDetail : Route("vod_detail/{vodId}") {
        fun withArgs(vodId: Long) = "vod_detail/$vodId"
    }
    object VodPlayer : Route("vod_player")
    object Series : Route("series")
    object SeriesDetail : Route("series_detail/{seriesId}") {
        fun withArgs(seriesId: Long) = "series_detail/$seriesId"
    }
    object Player : Route("player/{channelId}") {
        fun withArgs(channelId: Long) = "player/$channelId"
    }
}
