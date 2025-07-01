package com.se114.foodapp.data.repository

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.apiRequestFlow
import com.se114.foodapp.data.remote.CustomerApi
import com.se114.foodapp.domain.repository.CustomerRepository
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import javax.inject.Inject

class CustomerRepoImpl @Inject constructor(
    private val customerApi: CustomerApi,
) : CustomerRepository{
    override fun updateAvatar(
        customerId: String,
        avatar: MultipartBody.Part?,
    ): Flow<ApiResponse<Unit>> {
        return apiRequestFlow {
            customerApi.updateAvatar(customerId, avatar)
        }
    }
}