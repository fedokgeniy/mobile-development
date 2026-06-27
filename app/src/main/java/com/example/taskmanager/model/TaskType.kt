package com.example.taskmanager.model

/**
 * Fixed task types — selected at creation, never changed on edit.
 * Stored as String in Room for simplicity.
 */
enum class TaskType(val displayName: String) {
    STUDY("Study"),
    PERSONAL("Personal")
}
