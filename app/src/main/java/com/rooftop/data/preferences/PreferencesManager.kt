package com.rooftop.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    val epgUrl: Flow<String> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[EPG_URL_KEY] ?: "" }

    val favouriteIds: Flow<Set<String>> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[FAVOURITE_IDS_KEY] ?: emptySet() }

    suspend fun setEpgUrl(url: String) {
        dataStore.edit { it[EPG_URL_KEY] = url }
    }

    suspend fun toggleFavourite(contentId: String) {
        dataStore.edit { prefs ->
            val current = prefs[FAVOURITE_IDS_KEY] ?: emptySet()
            prefs[FAVOURITE_IDS_KEY] =
                if (contentId in current) current - contentId else current + contentId
        }
    }

    suspend fun isFavourite(contentId: String): Boolean =
        (dataStore.data.catch { emit(emptyPreferences()) }
            .map { it[FAVOURITE_IDS_KEY] ?: emptySet() }
            .let { flow ->
                var result = false
                flow.collect { result = contentId in it; return@collect }
                result
            })

    companion object {
        val EPG_URL_KEY = stringPreferencesKey("epg_url")
        val FAVOURITE_IDS_KEY = stringSetPreferencesKey("favourite_ids")
    }
}
