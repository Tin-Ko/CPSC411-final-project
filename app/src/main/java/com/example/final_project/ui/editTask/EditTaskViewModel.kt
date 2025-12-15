package com.example.final_project.ui.editTask

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.final_project.data.local.CategoryEntity
import com.example.final_project.data.local.TaskEntity
import com.example.final_project.data.repository.AuthRepository
import com.example.final_project.data.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditTaskViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // --- State for UI form fields ---
    var taskTitle by mutableStateOf("")
    var taskDueDate by mutableStateOf<Long?>(null)
    var taskNotes by mutableStateOf("")
    var selectedCategoryId by mutableStateOf<Int?>(null)

    // --- State for Categories and Dialogs ---
    var showNewCategoryDialog by mutableStateOf(false)
        private set // Keep setter private to control visibility from the ViewModel

    val categories: StateFlow<List<CategoryEntity>> =
        todoRepository.getCategories(authRepository.getCurrentUserId() ?: "")
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    private val taskId: Int = checkNotNull(savedStateHandle["taskId"])

    init {
        loadTaskDetails()
    }

    private fun loadTaskDetails() {
        viewModelScope.launch {
            todoRepository.getTaskById(taskId)?.let { taskWithCategory ->
                val task = taskWithCategory.task
                taskTitle = task.title
                taskDueDate = task.dueDate
                taskNotes = task.notes
                selectedCategoryId = task.categoryId
            }
        }
    }

    // --- Public functions for the UI to call ---

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

    fun updateTask(onTaskUpdated: () -> Unit) {
        val userId = authRepository.getCurrentUserId()
        if (taskTitle.isBlank() || userId == null) {
            return
        }

        viewModelScope.launch {
            // Fetch the original task once to preserve unchanged properties
            val originalTaskWithCategory = todoRepository.getTaskById(taskId) ?: return@launch
            val originalTaskEntity = originalTaskWithCategory.task

            val updatedTask = originalTaskEntity.copy(
//                id = taskId, // Ensure ID is preserved
                title = taskTitle,
                notes = taskNotes,
                dueDate = taskDueDate,
                categoryId = selectedCategoryId
            )
            todoRepository.updateTask(updatedTask)
            onTaskUpdated() // Navigate back
        }
    }
}
