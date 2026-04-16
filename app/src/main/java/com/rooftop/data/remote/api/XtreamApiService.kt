package com.rooftop.data.remote.api

import com.rooftop.data.remote.dto.XtreamLiveStreamDto
import com.rooftop.data.remote.dto.XtreamSeriesDto
import com.rooftop.data.remote.dto.XtreamSeriesInfoDto
import com.rooftop.data.remote.dto.XtreamVodInfoDto
import com.rooftop.data.remote.dto.XtreamVodStreamDto
import retrofit2.http.GET
import retrofit2.http.Url

interface XtreamApiService {
    @GET
    suspend fun getLiveStreams(@Url url: String): List<XtreamLiveStreamDto>

    @GET
    suspend fun getVodStreams(@Url url: String): List<XtreamVodStreamDto>

    @GET
    suspend fun getSeries(@Url url: String): List<XtreamSeriesDto>

    @GET
    suspend fun getVodInfo(@Url url: String): XtreamVodInfoDto

    @GET
    suspend fun getSeriesInfo(@Url url: String): XtreamSeriesInfoDto
}
