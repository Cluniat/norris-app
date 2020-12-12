package com.example.norris_app.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.norris_app.model.User

@Database(entities = [User::class], version = 2, exportSchema = false)
abstract class NorrisDatabase : RoomDatabase() {

    abstract val userDao: UserDao

    companion object {

        @Volatile
        private var INSTANCE: NorrisDatabase? = null

        fun getInstance(context: Context): NorrisDatabase {
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        NorrisDatabase::class.java,
                        "norris_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
                return INSTANCE as NorrisDatabase
            }
        }
    }
}