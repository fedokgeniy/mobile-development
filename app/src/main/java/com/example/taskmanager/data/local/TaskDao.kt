package com.example.taskmanager.data.local

import androidx.room.*
import com.example.taskmanager.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the "tasks" table.
 *
 * All write operations are suspend functions and must be called from a coroutine.
 * Read operations that return [Flow] are non-suspending; Room emits a new list
 * automatically whenever the underlying table changes.
 */
@Dao
interface TaskDao {

    // Read operations

    /**
     * Observes all tasks ordered by creation date, newest first.
     * The returned [Flow] emits a fresh list on every database change.
     */
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<TaskEntity>>

    /**
     * Observes tasks that belong to a specific type.
     *
     * @param type The string name of a [com.example.taskmanager.model.TaskType] enum value.
     */
    @Query("SELECT * FROM tasks WHERE type = :type ORDER BY createdAt DESC")
    fun observeByType(type: String): Flow<List<TaskEntity>>

    /**
     * Returns a single task by its primary key, or null if not found.
     * This is a one-shot suspend call, not a continuous stream.
     *
     * @param id The primary key of the task to fetch.
     */
    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): TaskEntity?

    // Write operations

    /**
     * Inserts a new task into the database.
     * [OnConflictStrategy.REPLACE] means an existing row with the same primary key
     * will be overwritten, which covers the upsert use case.
     *
     * @return The row ID of the newly inserted task.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity): Long

    /**
     * Updates an existing task row.
     * Callers are responsible for keeping the [TaskEntity.type] field unchanged.
     * For a safe update path that enforces this rule, use [TaskRepository.update].
     */
    @Update
    suspend fun update(task: TaskEntity)

    /**
     * Deletes the given task from the database.
     * Room matches the row by primary key.
     */
    @Delete
    suspend fun delete(task: TaskEntity)

    /**
     * Deletes a task by its primary key without requiring a full entity object.
     *
     * @param id The primary key of the task to delete.
     */
    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteById(id: Long)
}
