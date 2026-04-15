package com.rooftop.data.remote.dto

import com.google.gson.annotations.SerializedName

data class XtreamSeriesDto(
    @SerializedName("series_id") val seriesId: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("cover") val cover: String?,
    @SerializedName("category_id") val categoryId: String?,
    @SerializedName("rating") val rating: String?
)
