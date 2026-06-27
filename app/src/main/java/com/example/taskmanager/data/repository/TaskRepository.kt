package com.example.taskmanager.data.repository

import com.example.taskmanager.data.local.TaskDao
import com.example.taskmanager.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository that acts as the single source of truth for task data.
 *
 * ViewModels interact only with this class and are unaware of the underlying
 * DAO or database implementation. This separation makes the data layer easier
 * to test and replace independently.
 *
 * @param dao The Room DAO used to perform database operations.
 */
class TaskRepository(private val dao: TaskDao) {

    /** A live stream of all tasks, ordered by creation date. */
    val allTasks: Flow<List<TaskEntity>> = dao.observeAll()

    /**
     * Returns a live stream of tasks filtered by their type.
     *
     * @param type The string name of a TaskType enum value.
     */
    fun tasksByType(type: String): Flow<List<TaskEntity>> = dao.observeByType(type)

    /**
     * Fetches a single task by ID. Returns null if no task with that ID exists.
     *
     * @param id The primary key of the task.
     */
    suspend fun getById(id: Long): TaskEntity? = dao.getById(id)

    /**
     * Inserts a new task and returns its generated row ID.
     *
     * @param task The task to insert. The [TaskEntity.id] field should be 0
     *             so that Room auto-generates the primary key.
     */
    suspend fun insert(task: TaskEntity): Long = dao.insert(task)

    /**
     * Updates an existing task while enforcing that its type cannot change.
     *
     * The type is fetched from the database and copied back onto the entity
     * before the update, preventing accidental or intentional type mutation.
     *
     * @param task The task with updated fields. The [TaskEntity.type] value
     *             in this parameter is ignored; the stored type is always used.
     */
    suspend fun update(task: TaskEntity) {
        val existing = dao.getById(task.id) ?: return
        dao.update(task.copy(type = existing.type))
    }

    /**
     * Deletes the given task from the database.
     *
     * @param task The task to delete. Room matches it by primary key.
     */
    suspend fun delete(task: TaskEntity) = dao.delete(task)

    /**
     * Deletes a task by its primary key.
     *
     * @param id The primary key of the task to delete.
     */
    suspend fun deleteById(id: Long) = dao.deleteById(id)
}
