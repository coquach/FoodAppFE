package com.example.foodapp.domain.repository

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.filter.NotificationFilter
import com.example.foodapp.data.dto.request.FcmTokenRequest
import com.example.foodapp.data.model.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getAppNotifications(filter: NotificationFilter): Flow<ApiResponse<List<Notification>>>
    fun registerToken(request: FcmTokenRequest): Flow<ApiResponse<String>>
    fun readNotification(id: Long): Flow<ApiResponse<Unit>>
    fun removeToken(request: FcmTokenRequest): Flow<ApiResponse<String>>
    fun readAllNotification(userId: String): Flow<ApiResponse<Unit>>
}