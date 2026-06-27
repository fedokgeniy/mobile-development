package com.example.taskmanager.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.taskmanager.data.local.entity.TaskEntity

/**
 * The single Room database for the application.
 *
 * Declared as a singleton via the companion object to avoid creating multiple
 * database instances, which would be wasteful and could cause data inconsistencies.
 *
 * When the database schema changes, increment [version] and provide a
 * [androidx.room.migration.Migration] to preserve existing user data.
 */
@Database(
    entities = [TaskEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    /** Returns the DAO used to read and write tasks. */
    abstract fun taskDao(): TaskDao

    companion object {

        /**
         * Volatile ensures that the value of [INSTANCE] is always read from and
         * written to main memory, preventing stale cached values on other threads.
         */
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Returns the existing database instance or creates a new one if none exists.
         *
         * Uses double-checked locking to keep this thread-safe while avoiding
         * unnecessary synchronization overhead on every call.
         *
         * @param context Any context; applicationContext is used internally to
         *                avoid memory leaks from Activity or Fragment references.
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "task_manager.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
