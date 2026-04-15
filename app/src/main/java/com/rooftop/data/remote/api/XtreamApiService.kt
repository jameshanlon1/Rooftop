package com.rooftop.data.remote.api

import com.rooftop.data.remote.dto.XtreamLiveStreamDto
import com.rooftop.data.remote.dto.XtreamSeriesDto
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
}
