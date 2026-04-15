package com.rooftop.data.remote.mapper

import com.rooftop.data.remote.dto.XtreamLiveStreamDto
import com.rooftop.data.remote.dto.XtreamSeriesDto
import com.rooftop.data.remote.dto.XtreamVodStreamDto
import com.rooftop.domain.model.Channel
import com.rooftop.domain.model.Series
import com.rooftop.domain.model.StreamType
import com.rooftop.domain.model.VodItem

fun XtreamLiveStreamDto.toChannel(baseUrl: String, username: String, password: String): Channel =
    Channel(
        id = streamId?.toLong() ?: 0L,
        name = name.orEmpty(),
        streamUrl = "$baseUrl/live/$username/$password/$streamId.ts",
        logoUrl = streamIcon,
        groupTitle = categoryId,
        epgChannelId = epgChannelId,
        streamType = StreamType.LIVE
    )

fun XtreamVodStreamDto.toVodItem(baseUrl: String, username: String, password: String): VodItem =
    VodItem(
        id = streamId?.toLong() ?: 0L,
        name = name.orEmpty(),
        streamUrl = "$baseUrl/movie/$username/$password/$streamId.${containerExtension ?: "mp4"}",
        posterUrl = streamIcon,
        category = categoryId,
        rating = rating,
        year = year
    )

fun XtreamSeriesDto.toSeries(): Series =
    Series(
        id = seriesId?.toLong() ?: 0L,
        name = name.orEmpty(),
        coverUrl = cover,
        category = categoryId,
        rating = rating
    )
