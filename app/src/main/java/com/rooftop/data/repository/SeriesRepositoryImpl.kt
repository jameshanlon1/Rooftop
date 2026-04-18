package com.rooftop.data.repository

import com.rooftop.data.local.dao.SeriesDao
import com.rooftop.data.local.mapper.toDomain
import com.rooftop.data.local.mapper.toEntity
import com.rooftop.data.remote.api.XtreamApiService
import com.rooftop.data.remote.mapper.toSeries
import com.rooftop.domain.model.Episode
import com.rooftop.domain.model.Playlist
import com.rooftop.domain.model.Result
import com.rooftop.domain.model.Series
import com.rooftop.domain.model.SeriesDetail
import com.rooftop.domain.repository.SeriesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SeriesRepositoryImpl @Inject constructor(
    private val seriesDao: SeriesDao,
    private val xtreamApiService: XtreamApiService
) : SeriesRepository {

    override fun getSeriesItems(): Flow<List<Series>> =
        seriesDao.getAll().map { it.map { e -> e.toDomain() } }

    override fun getCategories(): Flow<List<String>> =
        seriesDao.getCategories()

    override fun getSeriesByCategory(category: String): Flow<List<Series>> =
        seriesDao.getByCategory(category).map { it.map { e -> e.toDomain() } }

    override suspend fun getSeriesDetail(seriesId: Long, playlist: Playlist): SeriesDetail? {
        val series = seriesDao.getById(seriesId)?.toDomain() ?: return null
        return try {
            val baseUrl = playlist.xtreamBaseUrl ?: return SeriesDetail(series, null, null, null, null, emptyMap())
            val username = playlist.xtreamUsername ?: return SeriesDetail(series, null, null, null, null, emptyMap())
            val password = playlist.xtreamPassword ?: return SeriesDetail(series, null, null, null, null, emptyMap())
            val url = "$baseUrl/player_api.php?username=$username&password=$password&action=get_series_info&series_id=$seriesId"
            val dto = xtreamApiService.getSeriesInfo(url)

            val seasons = dto.episodes
                ?.mapValues { (_, eps) ->
                    eps.mapNotNull { ep ->
                        val epId = ep.id?.toLongOrNull() ?: return@mapNotNull null
                        Episode(
                            id = epId,
                            seriesId = seriesId,
                            title = ep.title ?: "Episode ${ep.episodeNum}",
                            season = ep.season ?: 1,
                            episodeNum = ep.episodeNum ?: 0,
                            streamUrl = "$baseUrl/series/$username/$password/$epId.${ep.containerExtension ?: "mkv"}",
                            duration = ep.info?.duration,
                            plot = ep.info?.plot
                        )
                    }.sortedBy { it.episodeNum }
                }
                ?.mapKeys { it.key.toIntOrNull() ?: 0 }
                ?.toSortedMap()
                ?: emptyMap()

            SeriesDetail(
                series = series,
                plot = dto.info?.plot,
                cast = dto.info?.cast,
                genre = dto.info?.genre,
                backdropUrl = dto.info?.backdropPaths?.firstOrNull(),
                seasons = seasons
            )
        } catch (_: Exception) {
            SeriesDetail(series, null, null, null, null, emptyMap())
        }
    }

    override suspend fun refreshSeries(playlist: Playlist): Result<Unit> = try {
        val baseUrl = (playlist.xtreamBaseUrl ?: return Result.Error(Exception("Missing Xtream base URL"))).trimEnd('/')
        val username = playlist.xtreamUsername ?: return Result.Error(Exception("Missing username"))
        val password = playlist.xtreamPassword ?: return Result.Error(Exception("Missing password"))
        val url = "$baseUrl/player_api.php?username=$username&password=$password&action=get_series"
        val items = xtreamApiService.getSeries(url).map { it.toSeries() }
        seriesDao.deleteByPlaylist(playlist.id)
        seriesDao.insertAll(items.map { it.toEntity(playlist.id) })
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }
}
