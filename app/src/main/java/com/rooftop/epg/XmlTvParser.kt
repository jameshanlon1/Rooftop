package com.rooftop.epg

import android.util.Xml
import com.rooftop.domain.model.Programme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.zip.GZIPInputStream
import java.util.zip.ZipException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class XmlTvParser @Inject constructor() {

    private val dateFormat = SimpleDateFormat("yyyyMMddHHmmss Z", Locale.US)

    suspend fun parse(inputStream: InputStream): List<Programme> = withContext(Dispatchers.IO) {
        val stream = try {
            GZIPInputStream(inputStream)
        } catch (_: ZipException) {
            inputStream
        }

        val parser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(stream, null)

        val programmes = mutableListOf<Programme>()
        var event = parser.eventType

        while (event != XmlPullParser.END_DOCUMENT) {
            if (event == XmlPullParser.START_TAG && parser.name == "programme") {
                parseProgramme(parser)?.let { programmes.add(it) }
            }
            event = parser.next()
        }

        programmes
    }

    private fun parseProgramme(parser: XmlPullParser): Programme? {
        val channelId = parser.getAttributeValue(null, "channel") ?: return null
        val start = parseDate(parser.getAttributeValue(null, "start") ?: return null)
        val stop = parseDate(parser.getAttributeValue(null, "stop") ?: return null)

        var title = ""
        var description: String? = null
        var category: String? = null
        var iconUrl: String? = null

        var event = parser.next()
        while (!(event == XmlPullParser.END_TAG && parser.name == "programme")) {
            if (event == XmlPullParser.START_TAG) {
                when (parser.name) {
                    "title" -> title = readText(parser)
                    "desc" -> description = readText(parser)
                    "category" -> category = readText(parser)
                    "icon" -> {
                        iconUrl = parser.getAttributeValue(null, "src")
                        skipTag(parser, "icon")
                    }
                    else -> skipTag(parser, parser.name)
                }
            }
            event = parser.next()
        }

        if (title.isBlank()) return null
        return Programme(
            channelId = channelId,
            title = title,
            start = start,
            stop = stop,
            description = description,
            category = category,
            iconUrl = iconUrl
        )
    }

    private fun readText(parser: XmlPullParser): String {
        val sb = StringBuilder()
        var event = parser.next()
        while (event != XmlPullParser.END_TAG) {
            if (event == XmlPullParser.TEXT) sb.append(parser.text)
            event = parser.next()
        }
        return sb.toString().trim()
    }

    private fun skipTag(parser: XmlPullParser, tagName: String) {
        var depth = 1
        while (depth > 0) {
            when (parser.next()) {
                XmlPullParser.START_TAG -> if (parser.name == tagName) depth++
                XmlPullParser.END_TAG -> if (parser.name == tagName) depth--
            }
        }
    }

    private fun parseDate(dateStr: String): Long {
        val normalised = dateStr.trim().replace(":", "")
        return try {
            // Handle offset formats: "20240101120000 +0000" or "20240101120000 +0100"
            dateFormat.parse(normalised)?.time ?: 0L
        } catch (_: Exception) {
            try {
                // Some feeds omit the offset
                SimpleDateFormat("yyyyMMddHHmmss", Locale.US).parse(normalised.take(14))?.time ?: 0L
            } catch (_: Exception) {
                0L
            }
        }
    }
}
