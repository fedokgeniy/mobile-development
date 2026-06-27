package com.example.taskmanager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity — maps to the "tasks" table.
 *
 * @param type  Stored as String (name of TaskType enum). Fixed at creation.
 */
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val type: String,           // TaskType.name — never mutated after insert
    val priority: Int,          // 1 (Low) .. 3 (High)
    val deadline: String?,      // ISO date string or null
    val isDone: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
