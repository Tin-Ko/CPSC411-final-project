package com.example.final_project.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.final_project.data.local.TaskEntity
import com.example.final_project.data.local.TaskWithCategory
import com.example.final_project.data.repository.AuthRepository
import com.example.final_project.data.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val tasksByDate: StateFlow<Map<Long, List<TaskWithCategory>>> =
        todoRepository.getTasksWithDueDate(authRepository.getCurrentUserId() ?: "")
            .map { tasks ->
                tasks.groupBy { normalizeDate(it.task.dueDate!!) }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyMap()
            )


    fun updateTaskCompletion(task: TaskEntity, isCompleted: Boolean) {
        viewModelScope.launch {
            todoRepository.updateTask(task.copy(isCompleted = isCompleted))
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            todoRepository.deleteTask(task)
        }
    }

    private fun normalizeDate(dateMillis: Long): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = dateMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }
}
