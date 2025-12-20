package com.example.final_project.data.repository
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor() {
    private val auth = FirebaseAuth.getInstance()

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }


    suspend fun login(email: String, password: String): Result<Boolean> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUp(name: String, email: String, password: String): Result<Boolean> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()

            val user = auth.currentUser
            val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(name).build()
            user?.updateProfile(profileUpdates)?.await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() = auth.signOut()

    suspend fun updateUserProfilePicture(photoUri: Uri): Result<Boolean> {
        return try{
            val user = auth.currentUser
            val profileUpdates = UserProfileChangeRequest.Builder().setPhotoUri(photoUri).build()

            user?.updateProfile(profileUpdates)?.await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}