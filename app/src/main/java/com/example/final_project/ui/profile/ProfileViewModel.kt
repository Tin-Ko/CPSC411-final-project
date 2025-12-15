package com.example.final_project.ui.profile
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.final_project.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class ProfileUiState(
    val displayName: String = "",
    val email: String = "",
    val photoUrl: Uri? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel(){
    private val _uiState = MutableStateFlow(ProfileUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val user = authRepository.getCurrentUser()
        if (user != null) {
            _uiState.update {
                it.copy(
                    displayName = user.displayName ?: "No Name",
                    email = user.email ?: "No Email",
                    photoUrl = user.photoUrl,
                    isLoading = false,
                    error = null
                )
            }
        }
    }

    fun onPhotoSelected(uri: Uri?) {
        if (uri == null) return

        _uiState.update { it.copy(isLoading = true)}

        viewModelScope.launch {
            val result = authRepository.updateUserProfilePicture(uri)
            if (result.isSuccess) {
                loadUserProfile()
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
    }


}