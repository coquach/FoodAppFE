package com.se114.foodapp.domain.use_case.user

import com.example.foodapp.data.model.Account
import com.example.foodapp.domain.repository.AccountRepository
import com.example.foodapp.domain.use_case.auth.FirebaseResult
import com.example.foodapp.utils.StringUtils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LoadProfileUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
     operator fun invoke()= flow<FirebaseResult<Account>> {
         emit(FirebaseResult.Loading)
         try {
             val firebaseUser =
                 Firebase.auth.currentUser ?: throw Exception("User not logged in")
             val uid = firebaseUser.uid
             val docRef = Firebase.firestore.collection("users").document(uid)
             var userDoc = docRef.get().await()

             // Nếu user chưa có document (login bằng Google chẳng hạn)
             if (!userDoc.exists()) {
                 val newUser = mapOf(
                     "id" to firebaseUser.uid,
                     "displayName" to firebaseUser.displayName,
                     "email" to firebaseUser.email,
                     "avatar" to firebaseUser.photoUrl,
                     "phoneNumber" to "",
                     "gender" to "",
                     "dob" to ""
                 )
                 docRef.set(newUser).await()
                 userDoc = docRef.get().await() // load lại sau khi tạo
             }

             val baseAccount = accountRepository.getUserProfile()
             val accountLoaded = Account(
                 displayName = baseAccount.displayName,
                 email = baseAccount.email,
                 avatar = baseAccount.avatar,
                 phoneNumber = userDoc.data?.get("phoneNumber") as? String ?: "",
                 gender = userDoc.data?.get("gender") as? String ?: "",
                 dob = (userDoc.data?.get("dob") as? String)?.let {
                     StringUtils.parseLocalDate(
                         it
                     )
                 }
             )

             emit(FirebaseResult.Success(accountLoaded))

         } catch (e: Exception) {
             e.printStackTrace()
             emit(FirebaseResult.Failure("Lỗi không xác định"))
         }

     }.flowOn(Dispatchers.IO)

}