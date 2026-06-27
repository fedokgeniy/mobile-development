package com.example.taskmanager.model

/**
 * Represents the two fixed categories a task can belong to.
 *
 * The type is chosen when a task is created and cannot be changed when editing.
 * It is persisted in the Room database as the enum constant name (e.g., "STUDY").
 *
 * @param displayName The human-readable label shown in the UI.
 */
enum class TaskType(val displayName: String) {
    STUDY("Study"),
    PERSONAL("Personal")
}
