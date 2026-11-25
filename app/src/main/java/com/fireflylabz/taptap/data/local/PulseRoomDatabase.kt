package com.fireflylabz.taptap.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [NewsClusterEntity::class], version = 1, exportSchema = false)
abstract class PulseRoomDatabase : RoomDatabase() {
    abstract fun newsClusterDao(): NewsClusterDao

    companion object {
        @Volatile
        private var INSTANCE: PulseRoomDatabase? = null

        fun getDatabase(context: Context): PulseRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PulseRoomDatabase::class.java,
                    "pulse_room_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
