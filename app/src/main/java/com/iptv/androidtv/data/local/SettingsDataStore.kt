package com.iptv.androidtv.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val PLAYLIST_URL = stringPreferencesKey("playlist_url")
        val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
        val LAST_REFRESH = longPreferencesKey("last_refresh")
    }

    val playlistUrl: Flow<String> = dataStore.data.map { prefs ->
        prefs[PLAYLIST_URL] ?: ""
    }

    val isFirstLaunch: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[IS_FIRST_LAUNCH] ?: true
    }

    suspend fun setPlaylistUrl(url: String) {
        dataStore.edit { prefs ->
            prefs[PLAYLIST_URL] = url
            prefs[IS_FIRST_LAUNCH] = false
        }
    }

    suspend fun setLastRefresh(timestamp: Long) {
        dataStore.edit { prefs ->
            prefs[LAST_REFRESH] = timestamp
        }
    }
}
