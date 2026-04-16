package com.rooftop.domain.model

data class Programme(
    val id: Long = 0,
    val channelId: String,   // matches Channel.epgChannelId
    val title: String,
    val start: Long,         // epoch ms
    val stop: Long,          // epoch ms
    val description: String?,
    val category: String?,
    val iconUrl: String?,
    val hasReminder: Boolean = false
)
