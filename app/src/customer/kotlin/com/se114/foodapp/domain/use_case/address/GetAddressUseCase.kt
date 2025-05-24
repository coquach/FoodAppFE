package com.se114.foodapp.domain.use_case.address

import com.example.foodapp.data.model.Address
import com.example.foodapp.domain.use_case.auth.FirebaseResult
import com.example.foodapp.domain.use_case.auth.GetCustomerIdUseCase
import com.google.firebase.firestore.FirebaseFirestore
import com.se114.foodapp.ui.screen.address.AddressListViewModel.AddressState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GetAddressUseCase @Inject constructor(
    private val getCustomerIdUseCase: GetCustomerIdUseCase,
    private val firestore: FirebaseFirestore
) {
    operator fun invoke() = flow<FirebaseResult<List<Address>>> {
        emit(FirebaseResult.Loading)
        try {
            val userId = getCustomerIdUseCase()
            val addressesRef = firestore.collection("users")
                .document(userId)
                .collection("addresses")

            val querySnapshot = addressesRef.get().await()

            val addresses = querySnapshot.documents.mapNotNull { document ->
                if (document.id == "init") return@mapNotNull null
                val id = document.getString("id")
                val formatted = document.getString("formatted")
                val lat = document.getDouble("lat")
                val lon = document.getDouble("lon")

                if (id != null && formatted != null && lat != null && lon != null) {
                    Address(id, formatted, lat, lon)
                } else null
            }
            emit(FirebaseResult.Success(addresses))
        }catch (e: Exception){
            emit(FirebaseResult.Failure(e.message ?: "Lỗi khi nhận địa chỉ"))
        }
    }.flowOn(Dispatchers.IO)
}