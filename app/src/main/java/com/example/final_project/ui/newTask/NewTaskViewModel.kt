package com.example.final_project.ui.newTask

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.final_project.data.local.CategoryEntity
import com.example.final_project.data.local.TaskEntity
import com.example.final_project.data.repository.AuthRepository
import com.example.final_project.data.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewTaskViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    // UI state for the form fields
    var taskTitle by mutableStateOf("")
    var taskDueDate by mutableStateOf<Long?>(null)
    var taskNotes by mutableStateOf("")
    var selectedCategoryId by mutableStateOf<Int?>(null)

    var showNewCategoryDialog by mutableStateOf(false)
        private set

    // Live list of categories for the dropdown
    val categories: StateFlow<List<CategoryEntity>> =
        todoRepository.getCategories(authRepository.getCurrentUserId() ?: "")
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun onAddNewCategoryClicked() {
        showNewCategoryDialog = true
    }

    fun onNewCategoryDialogDismiss() {
        showNewCategoryDialog = false
    }

    fun createNewCategory(name: String, color: Color) {
        val userId = authRepository.getCurrentUserId()
        if (userId == null || name.isBlank()) return

        viewModelScope.launch {
            val newCategory = CategoryEntity(
                name = name,
                colorHex = color.value.toLong(),
                ownerId = userId
            )
            val newCategoryId = todoRepository.insertCategory(newCategory)
            selectedCategoryId = newCategoryId.toInt()
            showNewCategoryDialog = false
        }
    }

    fun onTitleChange(newTitle: String) {
        taskTitle = newTitle
    }

    fun onDueDateChange(newDueDate: Long?) {
        taskDueDate = newDueDate
    }

    fun onNotesChange(newNotes: String) {
        taskNotes = newNotes
    }

    fun onCategorySelected(categoryId: Int?) {
        selectedCategoryId = categoryId
    }

    fun saveTask(onTaskSaved: () -> Unit) {
        val userId = authRepository.getCurrentUserId()
        if (taskTitle.isBlank() || userId == null) {
            // Optionally, handle error state (e.g., show a toast)
            return
        }

        viewModelScope.launch {
            val newTask = TaskEntity(
                title = taskTitle,
                notes = taskNotes,
                createdAt = System.currentTimeMillis(),
                dueDate = taskDueDate,
                isCompleted = false,
                categoryId = selectedCategoryId,
                ownerId = userId,
                priority = 1 // Default priority
            )
            todoRepository.insertTask(newTask)
            onTaskSaved() // Use callback to navigate back
        }
    }
}
