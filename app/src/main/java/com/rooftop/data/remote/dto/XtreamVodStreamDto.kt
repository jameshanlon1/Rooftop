package com.rooftop.data.remote.dto

import com.google.gson.annotations.SerializedName

data class XtreamVodStreamDto(
    @SerializedName("stream_id") val streamId: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("stream_icon") val streamIcon: String?,
    @SerializedName("category_id") val categoryId: String?,
    @SerializedName("container_extension") val containerExtension: String?,
    @SerializedName("rating") val rating: String?,
    @SerializedName("year") val year: String?
)
