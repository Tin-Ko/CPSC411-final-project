package com.example.final_project.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.final_project.data.local.TaskEntity
import com.example.final_project.data.local.TaskWithCategory
import com.example.final_project.data.repository.AuthRepository
import com.example.final_project.data.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _tasks = MutableStateFlow<List<TaskWithCategory>>(emptyList())
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()
    val tasks: StateFlow<List<TaskWithCategory>> = _tasks.asStateFlow()


    init {
        loadTasks()
    }


    fun loadTasks() {
        val userId = authRepository.getCurrentUserId()
        if (userId != null) {
            viewModelScope.launch {
                todoRepository.getTasks(userId).collect { taskList ->
                    _tasks.value = taskList
                    _isLoading.value = false
                }
            }
        } else {
            _isLoading.value = false
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            todoRepository.deleteTask(task)
        }
    }

    fun updateTaskCompletion(task: TaskEntity, isCompleted: Boolean) {
        viewModelScope.launch {
            todoRepository.updateTask(task.copy(isCompleted = isCompleted))
        }
    }


}