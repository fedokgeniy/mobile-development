package com.example.taskmanager.data.repository

import com.example.taskmanager.data.local.TaskDao
import com.example.taskmanager.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for task data.
 * Hides DAO details from ViewModels.
 */
class TaskRepository(private val dao: TaskDao) {

    val allTasks: Flow<List<TaskEntity>> = dao.observeAll()

    fun tasksByType(type: String): Flow<List<TaskEntity>> = dao.observeByType(type)

    suspend fun getById(id: Long): TaskEntity? = dao.getById(id)

    suspend fun insert(task: TaskEntity): Long = dao.insert(task)

    /**
     * Update — enforces that type cannot change.
     * Fetches existing type from DB and overwrites the passed type field.
     */
    suspend fun update(task: TaskEntity) {
        val existing = dao.getById(task.id) ?: return
        dao.update(task.copy(type = existing.type))
    }

    suspend fun delete(task: TaskEntity) = dao.delete(task)

    suspend fun deleteById(id: Long) = dao.deleteById(id)
}
