package com.rooftop.data.repository

import com.rooftop.data.remote.api.XtreamApiService
import com.rooftop.data.remote.mapper.toChannel
import com.rooftop.data.remote.mapper.toSeries
import com.rooftop.data.remote.mapper.toVodItem
import com.rooftop.domain.model.Channel
import com.rooftop.domain.model.Playlist
import com.rooftop.domain.model.Result
import com.rooftop.domain.model.Series
import com.rooftop.domain.model.VodItem
import com.rooftop.domain.repository.XtreamRepository
import javax.inject.Inject

class XtreamRepositoryImpl @Inject constructor(
    private val xtreamApiService: XtreamApiService
) : XtreamRepository {

    override suspend fun getLiveStreams(playlist: Playlist): Result<List<Channel>> = try {
        val (baseUrl, username, password) = playlist.xtreamCredentials()
        val url = "$baseUrl/player_api.php?username=$username&password=$password&action=get_live_streams"
        Result.Success(xtreamApiService.getLiveStreams(url).map { it.toChannel(baseUrl, username, password) })
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun getVodStreams(playlist: Playlist): Result<List<VodItem>> = try {
        val (baseUrl, username, password) = playlist.xtreamCredentials()
        val url = "$baseUrl/player_api.php?username=$username&password=$password&action=get_vod_streams"
        Result.Success(xtreamApiService.getVodStreams(url).map { it.toVodItem(baseUrl, username, password) })
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun getSeries(playlist: Playlist): Result<List<Series>> = try {
        val (baseUrl, username, password) = playlist.xtreamCredentials()
        val url = "$baseUrl/player_api.php?username=$username&password=$password&action=get_series"
        Result.Success(xtreamApiService.getSeries(url).map { it.toSeries() })
    } catch (e: Exception) {
        Result.Error(e)
    }

    private data class XtreamCreds(val baseUrl: String, val username: String, val password: String)

    private fun Playlist.xtreamCredentials(): XtreamCreds {
        return XtreamCreds(
            baseUrl = xtreamBaseUrl ?: error("Missing Xtream base URL"),
            username = xtreamUsername ?: error("Missing Xtream username"),
            password = xtreamPassword ?: error("Missing Xtream password")
        )
    }
}
