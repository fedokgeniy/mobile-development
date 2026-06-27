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

class TaskDetailViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val repository = TaskRepository(
        AppDatabase.getInstance(application).taskDao()
    )

    // taskId passed via Navigation SafeArgs
    private val taskId: Long = savedStateHandle["taskId"] ?: -1L

    private val _task = MutableStateFlow<TaskEntity?>(null)
    val task: StateFlow<TaskEntity?> = _task

    init {
        if (taskId != -1L) loadTask()
    }

    private fun loadTask() {
        viewModelScope.launch {
            _task.value = repository.getById(taskId)
        }
    }

    fun deleteTask(onDeleted: () -> Unit) {
        viewModelScope.launch {
            _task.value?.let {
                repository.delete(it)
                onDeleted()
            }
        }
    }
}
