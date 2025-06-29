package com.se114.foodapp.domain.repository

import androidx.paging.PagingData
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.model.Feedback
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface FeedbackRepository {
    fun getFeedbackByOrderItemId(orderItemId: Long): Flow<ApiResponse<Feedback>>
    fun getFeedbacks(foodId: Long): Flow<PagingData<Feedback>>
    fun createFeedback(request: Map<String, @JvmSuppressWildcards RequestBody>, images: List<MultipartBody.Part?>? = null): Flow<ApiResponse<Feedback>>
    fun updateFeedback(id: Long, request: Map<String, @JvmSuppressWildcards RequestBody>, images: List<MultipartBody.Part?>? = null): Flow<ApiResponse<Feedback>>
    fun deleteFeedback(id: Long): Flow<ApiResponse<Unit>>
}