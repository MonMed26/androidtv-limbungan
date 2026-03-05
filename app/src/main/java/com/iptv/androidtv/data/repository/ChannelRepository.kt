package com.iptv.androidtv.data.repository

import com.iptv.androidtv.data.local.ChannelDao
import com.iptv.androidtv.data.local.SettingsDataStore
import com.iptv.androidtv.data.model.Channel
import com.iptv.androidtv.data.parser.M3UParser
import com.iptv.androidtv.data.remote.PlaylistService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChannelRepository @Inject constructor(
    private val channelDao: ChannelDao,
    private val playlistService: PlaylistService,
    private val m3uParser: M3UParser,
    private val settingsDataStore: SettingsDataStore
) {
    val allChannels: Flow<List<Channel>> = channelDao.getAllChannels()
    val groups: Flow<List<String>> = channelDao.getGroups()
    val favorites: Flow<List<Channel>> = channelDao.getFavorites()
    val playlistUrl: Flow<String> = settingsDataStore.playlistUrl
    val isFirstLaunch: Flow<Boolean> = settingsDataStore.isFirstLaunch

    fun getChannelsByGroup(group: String): Flow<List<Channel>> =
        channelDao.getChannelsByGroup(group)

    fun searchChannels(query: String): Flow<List<Channel>> =
        channelDao.searchChannels(query)

    suspend fun getChannelById(id: Long): Channel? =
        channelDao.getChannelById(id)

    suspend fun getChannelCount(): Int =
        channelDao.getChannelCount()

    suspend fun loadPlaylist(url: String): Result<Int> {
        return try {
            val content = playlistService.fetchPlaylist(url)
            val channels = m3uParser.parse(content)

            if (channels.isEmpty()) {
                Result.failure(Exception("No channels found in playlist"))
            } else {
                channelDao.deleteAll()
                channelDao.insertAll(channels)
                settingsDataStore.setPlaylistUrl(url)
                settingsDataStore.setLastRefresh(System.currentTimeMillis())
                Result.success(channels.size)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun refreshPlaylist(): Result<Int> {
        val url = settingsDataStore.playlistUrl.first()
        if (url.isEmpty()) return Result.failure(Exception("No playlist URL configured"))
        return loadPlaylist(url)
    }

    suspend fun toggleFavorite(channelId: Long) {
        channelDao.toggleFavorite(channelId)
    }
}
