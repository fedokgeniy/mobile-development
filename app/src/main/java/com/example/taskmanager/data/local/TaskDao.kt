package com.example.taskmanager.data.local

import androidx.room.*
import com.example.taskmanager.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    // ── Read ──────────────────────────────────────────────────────────────────

    /** Observe all tasks ordered by creation date. Flow emits on every DB change. */
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<TaskEntity>>

    /** Observe tasks filtered by type. */
    @Query("SELECT * FROM tasks WHERE type = :type ORDER BY createdAt DESC")
    fun observeByType(type: String): Flow<List<TaskEntity>>

    /** One-shot fetch of a single task by id. */
    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): TaskEntity?

    // ── Write ─────────────────────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity): Long

    /**
     * Update all fields EXCEPT type — the type is fixed after creation.
     * If you need to update a task, pass the same type value to stay safe.
     */
    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteById(id: Long)
}
