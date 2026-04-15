package com.rooftop.data.remote.dto

import com.google.gson.annotations.SerializedName

data class XtreamLiveStreamDto(
    @SerializedName("stream_id") val streamId: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("stream_icon") val streamIcon: String?,
    @SerializedName("epg_channel_id") val epgChannelId: String?,
    @SerializedName("category_id") val categoryId: String?,
    @SerializedName("stream_type") val streamType: String?,
    @SerializedName("tv_archive") val tvArchive: Int?,
    @SerializedName("direct_source") val directSource: String?
)
