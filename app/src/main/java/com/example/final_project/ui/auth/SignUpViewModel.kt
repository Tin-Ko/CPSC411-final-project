package com.example.final_project.ui.auth
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.final_project.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    fun signUpUser(name: String, email: String, password: String, confirmPassword: String, onResult: (Boolean, String?) -> Unit) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            onResult(false, "Please fill in all fields")
            return
        }

        if (password != confirmPassword) {
            onResult(false, "Passwords do not match")
            return
        }

        if (password.length < 6) {
            onResult(false, "Password must be at least 6 characters long")
            return
        }

        viewModelScope.launch {
            val result = authRepository.signUp(name, email, password)
            if (result.isSuccess) {
                onResult(true, null)
            } else {
                onResult(false, result.exceptionOrNull()?.message ?: "Sign up failed")
            }
        }
    }
}