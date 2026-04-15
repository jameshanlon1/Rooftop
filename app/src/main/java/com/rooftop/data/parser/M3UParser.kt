package com.rooftop.data.parser

import com.rooftop.domain.model.Channel
import com.rooftop.domain.model.StreamType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

class M3UParser @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    suspend fun parse(source: M3USource): List<Channel> {
        val content = when (source) {
            is M3USource.RemoteUrl -> withContext(Dispatchers.IO) { fetchContent(source.url) }
            is M3USource.LocalFile -> source.content
        }
        return parseContent(content)
    }

    private fun fetchContent(url: String): String {
        val request = Request.Builder().url(url).build()
        return okHttpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("Failed to fetch M3U: HTTP ${response.code}")
            response.body?.string() ?: throw Exception("Empty M3U response")
        }
    }

    private fun parseContent(content: String): List<Channel> {
        val lines = content.lines()
        if (lines.isEmpty() || !lines[0].trimStart().startsWith("#EXTM3U")) {
            throw Exception("Invalid M3U: missing #EXTM3U header")
        }

        val channels = mutableListOf<Channel>()
        var pendingMeta: ChannelMeta? = null
        var idCounter = 0L

        for (line in lines) {
            val trimmed = line.trim()
            when {
                trimmed.startsWith("#EXTINF:") -> pendingMeta = parseExtInf(trimmed)
                trimmed.isNotEmpty() && !trimmed.startsWith("#") -> {
                    if (pendingMeta != null) {
                        channels += Channel(
                            id = idCounter++,
                            name = pendingMeta.name,
                            streamUrl = trimmed,
                            logoUrl = pendingMeta.logoUrl,
                            groupTitle = pendingMeta.groupTitle,
                            epgChannelId = pendingMeta.tvgId,
                            streamType = StreamType.LIVE
                        )
                        pendingMeta = null
                    }
                }
            }
        }
        return channels
    }

    private fun parseExtInf(line: String): ChannelMeta {
        val tvgId = ATTR_TVG_ID.find(line)?.groupValues?.getOrNull(1)
        val tvgName = ATTR_TVG_NAME.find(line)?.groupValues?.getOrNull(1)
        val tvgLogo = ATTR_TVG_LOGO.find(line)?.groupValues?.getOrNull(1)
        val groupTitle = ATTR_GROUP_TITLE.find(line)?.groupValues?.getOrNull(1)
        val displayName = line.substringAfterLast(",").trim()
        return ChannelMeta(
            name = tvgName?.takeIf { it.isNotBlank() } ?: displayName,
            tvgId = tvgId?.takeIf { it.isNotBlank() },
            logoUrl = tvgLogo?.takeIf { it.isNotBlank() },
            groupTitle = groupTitle?.takeIf { it.isNotBlank() }
        )
    }

    private data class ChannelMeta(
        val name: String,
        val tvgId: String?,
        val logoUrl: String?,
        val groupTitle: String?
    )

    companion object {
        private val ATTR_TVG_ID = Regex("""tvg-id="([^"]*)"""")
        private val ATTR_TVG_NAME = Regex("""tvg-name="([^"]*)"""")
        private val ATTR_TVG_LOGO = Regex("""tvg-logo="([^"]*)"""")
        private val ATTR_GROUP_TITLE = Regex("""group-title="([^"]*)"""")
    }
}
