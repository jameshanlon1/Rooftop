package com.rooftop.data.parser

sealed class M3USource {
    data class RemoteUrl(val url: String) : M3USource()
    data class LocalFile(val content: String) : M3USource()
}
