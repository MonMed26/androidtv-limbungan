package com.iptv.androidtv.data.local

import androidx.room.*
import com.iptv.androidtv.data.model.Channel
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelDao {

    @Query("SELECT * FROM channels ORDER BY channelNumber ASC")
    fun getAllChannels(): Flow<List<Channel>>

    @Query("SELECT * FROM channels WHERE groupTitle = :group ORDER BY channelNumber ASC")
    fun getChannelsByGroup(group: String): Flow<List<Channel>>

    @Query("SELECT DISTINCT groupTitle FROM channels WHERE groupTitle IS NOT NULL ORDER BY groupTitle ASC")
    fun getGroups(): Flow<List<String>>

    @Query("SELECT * FROM channels WHERE isFavorite = 1 ORDER BY channelNumber ASC")
    fun getFavorites(): Flow<List<Channel>>

    @Query("SELECT * FROM channels WHERE name LIKE '%' || :query || '%' ORDER BY channelNumber ASC")
    fun searchChannels(query: String): Flow<List<Channel>>

    @Query("SELECT * FROM channels WHERE id = :id")
    suspend fun getChannelById(id: Long): Channel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(channels: List<Channel>)

    @Update
    suspend fun updateChannel(channel: Channel)

    @Query("DELETE FROM channels")
    suspend fun deleteAll()

    @Query("UPDATE channels SET isFavorite = NOT isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: Long)

    @Query("SELECT COUNT(*) FROM channels")
    suspend fun getChannelCount(): Int
}
