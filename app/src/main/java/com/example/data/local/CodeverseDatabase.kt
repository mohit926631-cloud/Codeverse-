package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ProjectEntity::class, CompletedLessonEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CodeverseDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun completedLessonDao(): CompletedLessonDao

    companion object {
        @Volatile
        private var INSTANCE: CodeverseDatabase? = null

        fun getInstance(context: Context): CodeverseDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    CodeverseDatabase::class.java,
                    "codeverse_local.db"
                ).fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
