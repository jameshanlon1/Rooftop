package com.rooftop.data.repository

import com.rooftop.data.local.dao.VodDao
import com.rooftop.data.local.mapper.toDomain
import com.rooftop.data.local.mapper.toEntity
import com.rooftop.data.remote.api.XtreamApiService
import com.rooftop.data.remote.mapper.toVodItem
import com.rooftop.domain.model.Playlist
import com.rooftop.domain.model.Result
import com.rooftop.domain.model.VodInfo
import com.rooftop.domain.model.VodItem
import com.rooftop.domain.repository.VodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class VodRepositoryImpl @Inject constructor(
    private val vodDao: VodDao,
    private val xtreamApiService: XtreamApiService
) : VodRepository {

    override fun getVodItems(): Flow<List<VodItem>> =
        vodDao.getAll().map { it.map { e -> e.toDomain() } }

    override fun getCategories(): Flow<List<String>> =
        vodDao.getCategories()

    override fun getVodByCategory(category: String): Flow<List<VodItem>> =
        vodDao.getByCategory(category).map { it.map { e -> e.toDomain() } }

    override suspend fun getVodInfo(vodId: Long, playlist: Playlist): VodInfo? {
        val item = vodDao.getById(vodId)?.toDomain() ?: return null
        return try {
            val baseUrl = playlist.xtreamBaseUrl ?: return VodInfo(item, null, null, null, null, null)
            val username = playlist.xtreamUsername ?: return VodInfo(item, null, null, null, null, null)
            val password = playlist.xtreamPassword ?: return VodInfo(item, null, null, null, null, null)
            val url = "$baseUrl/player_api.php?username=$username&password=$password&action=get_vod_info&vod_id=$vodId"
            val dto = xtreamApiService.getVodInfo(url)
            VodInfo(
                item = item,
                plot = dto.info?.plot,
                cast = dto.info?.cast,
                director = dto.info?.director,
                genre = dto.info?.genre,
                backdropUrl = dto.info?.backdropPaths?.firstOrNull()
            )
        } catch (_: Exception) {
            VodInfo(item, null, null, null, null, null)
        }
    }

    override suspend fun refreshVod(playlist: Playlist): Result<Unit> = try {
        val baseUrl = (playlist.xtreamBaseUrl ?: return Result.Error(Exception("Missing Xtream base URL"))).trimEnd('/')
        val username = playlist.xtreamUsername ?: return Result.Error(Exception("Missing username"))
        val password = playlist.xtreamPassword ?: return Result.Error(Exception("Missing password"))
        val url = "$baseUrl/player_api.php?username=$username&password=$password&action=get_vod_streams"
        val items = xtreamApiService.getVodStreams(url).map { it.toVodItem(baseUrl, username, password) }
        vodDao.deleteByPlaylist(playlist.id)
        vodDao.insertAll(items.map { it.toEntity(playlist.id) })
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }
}
