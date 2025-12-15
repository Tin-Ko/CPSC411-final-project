package com.example.final_project.ui
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.final_project.data.repository.AuthRepository
import com.example.final_project.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _startDestination = MutableStateFlow(Screen.Login.route)
    val startDestination = _startDestination.asStateFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        val isLoggedIn = authRepository.isUserLoggedIn()

        if (isLoggedIn) {
            _startDestination.value = Screen.Home.route
        } else {
            _startDestination.value = Screen.Login.route
        }

        viewModelScope.launch {
//            delay(200)
            _isLoading.value = false
        }
    }
}