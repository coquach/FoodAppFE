package com.se114.foodapp.domain.use_case.address

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.domain.use_case.auth.FirebaseResult
import com.example.foodapp.domain.use_case.auth.GetCustomerIdUseCase
import com.google.firebase.firestore.FirebaseFirestore
import com.se114.foodapp.ui.screen.address.AddressListViewModel.AddressState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DeleteAddressUseCase @Inject constructor(
    private val getCustomerIdUseCase: GetCustomerIdUseCase,
    private val firestore: FirebaseFirestore,
) {
    operator fun invoke(addressId: String) = flow<FirebaseResult<Unit>> {
        emit(FirebaseResult.Loading)
        try {
            val userId = getCustomerIdUseCase()
            val addressRef = firestore.collection("users")
                .document(userId)
                .collection("addresses")
                .document(addressId)

            addressRef.delete().await()
            emit(FirebaseResult.Success(Unit))

        } catch (e: Exception) {
            emit(FirebaseResult.Failure(e.message ?: "Lỗi khi xóa địa chỉ"))

        }

    }.flowOn(Dispatchers.IO)
}