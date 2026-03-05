package com.iptv.androidtv.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.iptv.androidtv.data.model.Channel

@Database(entities = [Channel::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun channelDao(): ChannelDao
}
