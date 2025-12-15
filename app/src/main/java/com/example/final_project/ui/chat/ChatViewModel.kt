package com.example.final_project.ui.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.final_project.data.repository.AuthRepository
import com.example.final_project.data.repository.ChatRepository
import com.example.final_project.data.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

// A simple data class to represent a chat message
data class ChatMessage(
    val message: String,
    val isFromUser: Boolean
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val todoRepository: TodoRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    var messages by mutableStateOf<List<ChatMessage>>(emptyList())
        private set

    var currentMessage by mutableStateOf("")
        private set

    var isLoading = _isLoading.asStateFlow()

    init {
        // Add a welcome message from the bot
        messages = listOf(ChatMessage("Hello! How can I help you with your tasks today?", isFromUser = false))
        _isLoading.value = false
    }

    fun onMessageChange(newMessage: String) {
        currentMessage = newMessage
    }

    fun sendMessage() {
        if (currentMessage.isBlank() || _isLoading.value) return

        val userMessage = ChatMessage(currentMessage, true)
        messages = messages + userMessage
        val messageToSend = currentMessage
        currentMessage = ""

        viewModelScope.launch {
            _isLoading.value = true

            val tasks = todoRepository.getTasks(authRepository.getCurrentUserId()!!).first()
            val taskContext = if (tasks.isEmpty()) {
                "The user has no tasks."
            } else {
                tasks.joinToString("\n") { taskWithCategory ->
                    val task = taskWithCategory.task
                    val cat = taskWithCategory.category?.name ?: "No Category"
                    val due = task.dueDate?.let { "Due: ${formatDate(it)}" } ?: ""
                    "- Title: ${task.title}, Completed: ${task.isCompleted}, Category: $cat, $due"
                }
            }

            val result = chatRepository.getChatCompletion(messageToSend, taskContext)

            val botResponse = result.getOrNull()?.let {
                ChatMessage(it, false)
            } ?: ChatMessage("Sorry, something went wrong. Please try again.", false)

            messages = messages + botResponse
            _isLoading.value = false
        }
    }

    private fun formatDate(millis: Long): String {
        val formatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        return formatter.format(Date(millis))
    }
}
