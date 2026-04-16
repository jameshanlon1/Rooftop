package com.rooftop.data.remote.dto

import com.google.gson.annotations.SerializedName

data class XtreamVodInfoDto(
    @SerializedName("info") val info: VodInfoDetailDto?
)

data class VodInfoDetailDto(
    @SerializedName("plot") val plot: String?,
    @SerializedName("cast") val cast: String?,
    @SerializedName("director") val director: String?,
    @SerializedName("genre") val genre: String?,
    @SerializedName("backdrop_path") val backdropPaths: List<String>?,
    @SerializedName("rating_5based") val rating: Double?
)
