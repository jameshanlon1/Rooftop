package com.rooftop.data.repository

import com.rooftop.data.local.dao.ChannelDao
import com.rooftop.data.local.entity.ChannelEntity
import com.rooftop.data.local.mapper.toDomain
import com.rooftop.data.local.mapper.toEntity
import com.rooftop.data.parser.M3UParser
import com.rooftop.data.parser.M3USource
import com.rooftop.data.remote.api.XtreamApiService
import com.rooftop.data.remote.mapper.toChannel
import com.rooftop.domain.model.Channel
import com.rooftop.domain.model.PlaylistType
import com.rooftop.domain.model.Result
import com.rooftop.domain.repository.ChannelRepository
import com.rooftop.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChannelRepositoryImpl @Inject constructor(
    private val channelDao: ChannelDao,
    private val playlistRepository: PlaylistRepository,
    private val m3uParser: M3UParser,
    private val xtreamApiService: XtreamApiService
) : ChannelRepository {

    // TODO: remove before release — seeds one test channel when DB is empty
    private suspend fun seedTestChannelIfEmpty() {
        if (channelDao.getCount() == 0) {
            channelDao.insertAll(
                listOf(
                    ChannelEntity(
                        id = 0,
                        name = "Test Stream",
                        streamUrl = "https://netanyahu.modifiles.fans/secure/tsFxHWxTwuEphJbkFZqcpuilIWXeJICM/1776355200/1776382200/uel_avl/tracks-v1a1/mono.ts.m3u8",
                        logoUrl = null,
                        groupTitle = "Test",
                        epgChannelId = null,
                        streamType = "LIVE",
                        playlistId = 0L
                    )
                )
            )
        }
    }

    override fun getChannels(): Flow<List<Channel>> =
        channelDao.getAll().onStart { seedTestChannelIfEmpty() }
            .map { it.map { entity -> entity.toDomain() } }

    override fun getChannelsByGroup(group: String): Flow<List<Channel>> =
        channelDao.getByGroup(group).map { it.map { entity -> entity.toDomain() } }

    override suspend fun refreshChannels(playlistId: Long): Result<Unit> = try {
        val playlist = playlistRepository.getPlaylistById(playlistId)
            ?: return Result.Error(Exception("Playlist $playlistId not found"))

        val channels: List<Channel> = when (playlist.type) {
            PlaylistType.M3U -> {
                val url = playlist.url ?: return Result.Error(Exception("M3U URL is missing"))
                m3uParser.parse(M3USource.RemoteUrl(url))
            }
            PlaylistType.XTREAM -> {
                val baseUrl = (playlist.xtreamBaseUrl ?: return Result.Error(Exception("Xtream base URL missing"))).trimEnd('/')
                val username = playlist.xtreamUsername ?: return Result.Error(Exception("Xtream username missing"))
                val password = playlist.xtreamPassword ?: return Result.Error(Exception("Xtream password missing"))
                val url = "$baseUrl/player_api.php?username=$username&password=$password&action=get_live_streams"
                xtreamApiService.getLiveStreams(url).map { it.toChannel(baseUrl, username, password) }
            }
        }

        channelDao.deleteByPlaylist(playlistId)
        channelDao.insertAll(channels.map { it.toEntity(playlistId) })
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun getChannelById(id: Long): Channel? =
        channelDao.getById(id)?.toDomain()

    override suspend fun clearChannels(playlistId: Long) {
        channelDao.deleteByPlaylist(playlistId)
    }
}
