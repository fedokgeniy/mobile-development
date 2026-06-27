package com.example.taskmanager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity that maps to the "tasks" table in the local SQLite database.
 *
 * The [type] field stores the name of the [com.example.taskmanager.model.TaskType] enum.
 * It is set once on insertion and must never be changed during an update operation.
 *
 * [priority] uses an integer scale: 1 = Low, 2 = Medium, 3 = High.
 * [deadline] is stored as an ISO-8601 date string (yyyy-MM-dd) or null when not set.
 * [createdAt] is a Unix timestamp in milliseconds, assigned automatically at creation.
 */
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val type: String,
    val priority: Int,
    val deadline: String?,
    val isDone: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
