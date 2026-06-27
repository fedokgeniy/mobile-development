package com.example.taskmanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.data.local.AppDatabase
import com.example.taskmanager.data.local.entity.TaskEntity
import com.example.taskmanager.data.repository.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TaskRepository(
        AppDatabase.getInstance(application).taskDao()
    )

    /** Hot StateFlow — survives configuration changes (screen rotation). */
    val tasks: StateFlow<List<TaskEntity>> = repository.allTasks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch { repository.delete(task) }
    }
}
