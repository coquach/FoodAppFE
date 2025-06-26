package com.example.foodapp.domain.repository

import android.net.Uri
import com.example.foodapp.data.model.Account
import com.google.firebase.auth.UserProfileChangeRequest

import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    val currentUser: Flow<Account?>
    val currentUserId: String?
    suspend fun getUserToken(): String?
    fun hasUser(): Boolean
    fun getUserProfile(): Account
    suspend fun isEmailVerified(): Boolean
    fun isGoogleLinked(): Boolean
    fun sendVerifyEmail()
    suspend fun reloadToken()
    suspend fun createAccountWithEmail(email: String, password: String)
    suspend fun updateProfile(photoUrl: Uri?, name: String)
    suspend fun linkAccountWithGoogle(idToken: String)
    suspend fun linkAccountWithEmail(email: String, password: String)
    suspend fun signInWithGoogle(idToken: String)
    suspend fun signInWithEmail(email: String, password: String)
    suspend fun forgetPassword(email: String)
    suspend fun resetPassword(obb : String, newPassword: String)
    suspend fun updatePassword(newPassword: String)
    suspend fun reAuthenticateWithEmail(email: String, password: String)
    suspend fun reAuthenticateWithGoogle(idToken: String)
    suspend fun signOut()
    suspend fun deleteAccount()
    suspend fun getUserRole(): String?
}