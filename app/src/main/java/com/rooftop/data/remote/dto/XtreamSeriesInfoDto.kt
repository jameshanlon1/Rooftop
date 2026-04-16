package com.rooftop.data.remote.dto

import com.google.gson.annotations.SerializedName

data class XtreamSeriesInfoDto(
    @SerializedName("info") val info: SeriesInfoDetailDto?,
    @SerializedName("episodes") val episodes: Map<String, List<XtreamEpisodeDto>>?
)

data class SeriesInfoDetailDto(
    @SerializedName("plot") val plot: String?,
    @SerializedName("cast") val cast: String?,
    @SerializedName("genre") val genre: String?,
    @SerializedName("backdrop_path") val backdropPaths: List<String>?,
    @SerializedName("rating_5based") val rating: Double?
)

data class XtreamEpisodeDto(
    @SerializedName("id") val id: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("episode_num") val episodeNum: Int?,
    @SerializedName("season") val season: Int?,
    @SerializedName("container_extension") val containerExtension: String?,
    @SerializedName("info") val info: EpisodeInfoDto?
)

data class EpisodeInfoDto(
    @SerializedName("plot") val plot: String?,
    @SerializedName("duration") val duration: String?
)
