package com.example.foodapp.data.service


import com.example.foodapp.data.model.Account
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.Flow

interface AccountService {
    val currentUser: Flow<Account?>
    val currentUserId: String?
    suspend fun getUserToken(): String?
    fun hasUser(): Boolean
    fun getUserProfile(): Account
    fun isEmailVerified(): Boolean
    fun sendVerifyEmail()
    suspend fun reloadToken()
    suspend fun createAccountWithEmail(email: String, password: String)
    suspend fun updateProfile(updateProfile: UserProfileChangeRequest)
    suspend fun linkAccountWithGoogle(idToken: String)
    suspend fun linkAccountWithEmail(email: String, password: String)
    suspend fun signInWithGoogle(idToken: String)
    suspend fun signInWithEmail(email: String, password: String)
    suspend fun forgetPassword(email: String)
    suspend fun resetPassword(obb : String, newPassword: String)
    suspend fun signOut()
    suspend fun deleteAccount()
    suspend fun getUserRole(): String?
}