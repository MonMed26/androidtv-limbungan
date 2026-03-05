package com.iptv.androidtv.data.parser

import com.iptv.androidtv.data.model.Channel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class M3UParser @Inject constructor() {

    fun parse(content: String): List<Channel> {
        val channels = mutableListOf<Channel>()
        val lines = content.lines()
        var i = 0
        var channelNumber = 1

        while (i < lines.size) {
            val line = lines[i].trim()

            if (line.startsWith("#EXTINF:")) {
                val tvgId = extractAttribute(line, "tvg-id")
                val tvgName = extractAttribute(line, "tvg-name")
                val tvgLogo = extractAttribute(line, "tvg-logo")
                val groupTitle = extractAttribute(line, "group-title")
                val displayName = extractDisplayName(line)

                // Find the stream URL (next non-empty, non-comment line)
                i++
                while (i < lines.size && (lines[i].isBlank() || lines[i].trim().startsWith("#"))) {
                    i++
                }

                if (i < lines.size) {
                    val streamUrl = lines[i].trim()
                    if (streamUrl.isNotEmpty()) {
                        channels.add(
                            Channel(
                                name = displayName.ifEmpty { tvgName ?: "Channel $channelNumber" },
                                url = streamUrl,
                                logoUrl = tvgLogo,
                                groupTitle = groupTitle ?: "Uncategorized",
                                tvgId = tvgId,
                                tvgName = tvgName,
                                channelNumber = channelNumber
                            )
                        )
                        channelNumber++
                    }
                }
            }
            i++
        }

        return channels
    }

    private fun extractAttribute(line: String, attribute: String): String? {
        val regex = """$attribute="([^"]*)"""".toRegex()
        return regex.find(line)?.groupValues?.get(1)?.takeIf { it.isNotEmpty() }
    }

    private fun extractDisplayName(line: String): String {
        val commaIndex = line.lastIndexOf(",")
        return if (commaIndex >= 0) {
            line.substring(commaIndex + 1).trim()
        } else {
            ""
        }
    }
}
