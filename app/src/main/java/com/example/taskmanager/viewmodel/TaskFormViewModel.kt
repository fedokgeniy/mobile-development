package com.example.taskmanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.data.local.AppDatabase
import com.example.taskmanager.data.local.entity.TaskEntity
import com.example.taskmanager.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskFormViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val repository = TaskRepository(
        AppDatabase.getInstance(application).taskDao()
    )

    // -1 means CREATE mode; any other value means EDIT mode
    val taskId: Long = savedStateHandle["taskId"] ?: -1L
    val isEditMode: Boolean get() = taskId != -1L

    private val _existingTask = MutableStateFlow<TaskEntity?>(null)
    val existingTask: StateFlow<TaskEntity?> = _existingTask

    init {
        if (isEditMode) {
            viewModelScope.launch {
                _existingTask.value = repository.getById(taskId)
            }
        }
    }

    /**
     * Save — insert or update depending on mode.
     * Type is taken from existingTask in edit mode (repository enforces this too).
     */
    fun save(
        title: String,
        description: String,
        type: String,
        priority: Int,
        deadline: String?,
        isDone: Boolean,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            if (isEditMode) {
                val existing = _existingTask.value ?: return@launch
                repository.update(
                    existing.copy(
                        title = title,
                        description = description,
                        priority = priority,
                        deadline = deadline,
                        isDone = isDone
                        // type intentionally omitted — repository re-applies original type
                    )
                )
            } else {
                repository.insert(
                    TaskEntity(
                        title = title,
                        description = description,
                        type = type,
                        priority = priority,
                        deadline = deadline,
                        isDone = isDone
                    )
                )
            }
            onSuccess()
        }
    }
}
