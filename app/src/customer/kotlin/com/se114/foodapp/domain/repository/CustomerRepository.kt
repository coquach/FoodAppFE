package com.se114.foodapp.domain.repository

import com.example.foodapp.data.dto.ApiResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface CustomerRepository {
    fun updateAvatar(customerId: String, avatar: MultipartBody.Part): Flow<ApiResponse<Unit>>
}