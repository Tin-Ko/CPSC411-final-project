package com.example.final_project.ui.home
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
import android.net.Uri

enum class HomeTab {
    Tasks, Calendar, Chat
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _selectedTab = MutableStateFlow(HomeTab.Tasks)
    private val _topBarTitle = MutableStateFlow("My Tasks")
    val selectedTab: StateFlow<HomeTab> = _selectedTab.asStateFlow()
    val topBarTitle: StateFlow<String> = _topBarTitle.asStateFlow()

    fun onTabSelected(tab: HomeTab) {
        _selectedTab.value = tab
    }

    fun updateTopBarTitle(title: String) {
        _topBarTitle.value = title
    }

}