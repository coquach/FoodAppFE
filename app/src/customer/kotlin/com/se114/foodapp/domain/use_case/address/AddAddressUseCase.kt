package com.se114.foodapp.domain.use_case.address

import com.example.foodapp.data.model.AddressUI
import com.example.foodapp.domain.use_case.auth.FirebaseResult
import com.example.foodapp.domain.use_case.auth.GetUserIdUseCase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AddAddressUseCase @Inject constructor(
    private val getUserIdUseCase: GetUserIdUseCase,
    private val firestore: FirebaseFirestore
) {
    operator fun invoke(address: AddressUI) = flow<FirebaseResult<Unit>> {
        emit(FirebaseResult.Loading)
        try {
            val userId = getUserIdUseCase()
            val addressRef = firestore.collection("users")
                .document(userId)
                .collection("addresses")
                .document(address.id)

            val addressMap = hashMapOf(
                "id" to address.id,
                "formatted" to address.formatAddress,
                "lat" to address.latitude,
                "lon" to address.longitude
            )

            addressRef.set(addressMap).await()
            emit(FirebaseResult.Success(Unit))
        }catch (e: Exception){
            emit(FirebaseResult.Failure(e.message ?: "Lỗi khi thêm địa chỉ"))

        }
    }.flowOn(Dispatchers.IO)
}