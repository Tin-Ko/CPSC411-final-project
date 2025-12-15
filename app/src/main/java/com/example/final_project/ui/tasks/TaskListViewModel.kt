package com.example.final_project.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.final_project.data.local.CategoryEntity
import com.example.final_project.data.local.TaskEntity
import com.example.final_project.data.local.TaskWithCategory
import com.example.final_project.data.repository.AuthRepository
import com.example.final_project.data.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _tasks = MutableStateFlow<List<TaskWithCategory>>(emptyList())
    private val _categories = MutableStateFlow<List<CategoryEntity>>(emptyList())
    private val _showFilterDialog = MutableStateFlow(false)
    private val _selectedCategoryIds = MutableStateFlow<Set<Int>>(emptySet())
    val showFilterDialog: StateFlow<Boolean> = _showFilterDialog.asStateFlow()
    val selectedCategoryIds: StateFlow<Set<Int>> = _selectedCategoryIds.asStateFlow()

    val tasks: StateFlow<List<TaskWithCategory>> =
        todoRepository.getTasks(authRepository.getCurrentUserId() ?: "")
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<CategoryEntity>> = tasks.map { tasksWithCategory ->
        tasksWithCategory
            .mapNotNull { it.category }
            .distinctBy { it.id }
            .sortedBy { it.name }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isLoading: StateFlow<Boolean> = tasks.map { false }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )


    val filteredTasks: StateFlow<List<TaskWithCategory>> =
        combine(tasks, selectedCategoryIds) { tasks, selectedIds ->
            if (selectedIds.isEmpty()) {
                tasks
            } else {
                tasks.filter { it.task.categoryId in selectedIds }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

//    init {
//        loadTasks()
//    }
//
//
//    fun loadTasks() {
//        val userId = authRepository.getCurrentUserId()
//        if (userId != null) {
//            viewModelScope.launch {
//                todoRepository.getTasks(userId).collect { taskList ->
//                    _tasks.value = taskList
//                    _categories.value = _tasks.value.mapNotNull { it.category }.distinctBy { it.id }
//                    _isLoading.value = false
//                }
//            }
//        } else {
//            _isLoading.value = false
//        }
//    }

    fun onFilterClicked() {
        _showFilterDialog.value = true
    }

    fun onFilterDialogDismiss() {
        _showFilterDialog.value = false
    }

    fun onFilterDialogConfirm() {
        _showFilterDialog.value = false
    }

    fun onFilterCategorySelected(categoryId: Int, isSelected: Boolean) {
        val currentIds = _selectedCategoryIds.value.toMutableSet()
        if (isSelected) {
            currentIds.add(categoryId)
        } else {
            currentIds.remove(categoryId)
        }
        _selectedCategoryIds.value = currentIds
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