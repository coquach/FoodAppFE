package com.se114.foodapp.domain.use_case.user

import android.content.Context
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.model.Account
import com.example.foodapp.domain.repository.AccountRepository
import com.example.foodapp.domain.use_case.auth.FirebaseResult
import com.example.foodapp.utils.ImageUtils
import com.example.foodapp.utils.StringUtils
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.se114.foodapp.domain.repository.CustomerRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val customerRepository: CustomerRepository,
    @ApplicationContext private val context: Context
) {
    operator fun invoke(profile: Account) = flow<FirebaseResult<Unit>> {
        emit(FirebaseResult.Loading)
        try {
            val uid = accountRepository.currentUserId ?: throw Exception("User not logged in")
            val uri = profile.avatar
            if (uri != null) {
                val avatar = ImageUtils.getImagePart(context, uri, "avatar")
                val result = customerRepository.updateAvatar(uid, avatar)
                    .first { it !is ApiResponse.Loading }

                if (result is ApiResponse.Failure) {
                    throw Exception(result.errorMessage)
                }
            }
            accountRepository.updateProfile(profile.displayName)
            saveUserToFireStore(uid, profile)
            emit(FirebaseResult.Success(Unit))
        } catch (e: Exception) {
            emit(FirebaseResult.Failure(e.message ?: "Lỗi không xác định"))
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun saveUserToFireStore(uid: String, profile: Account) { // Make it suspend
        val userMap = hashMapOf(
            "id" to profile.id,
            "displayName" to profile.displayName,
            "phoneNumber" to profile.phoneNumber,
            "gender" to profile.gender,
            "dob" to StringUtils.formatLocalDate(profile.dob),
        )

        try {
            Firebase.firestore.collection("users").document(uid)
                .set(userMap, SetOptions.merge())
                .await() // Use await
            //Create an initial empty address document for the user.
            // This might be used to ensure the 'addresses' subcollection exists
            // or to provide a placeholder for the user to add their first address.
            val dummyAddress = hashMapOf(
                "id" to "init",
                "formatted" to "",
                "lat" to 0.0,
                "lon" to 0.0
            )

            Firebase.firestore.collection("users")
                .document(uid)
                .collection("addresses")
                .document("init")
                .set(dummyAddress)
                .await() // Use await

        }  catch (e: FirebaseFirestoreException) {
            // Let the exception propagate or wrap it in a custom exception
            throw FirebaseFirestoreException(
                "Không thể lưu dữ liệu vào hệ thống: ${e.message}",
                e.code
            ) as Throwable
        }
    }
}