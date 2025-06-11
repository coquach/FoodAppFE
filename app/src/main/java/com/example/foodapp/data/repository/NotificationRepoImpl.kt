package com.example.foodapp.data.repository

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.apiRequestFlow
import com.example.foodapp.data.dto.filter.NotificationFilter
import com.example.foodapp.data.dto.request.FcmTokenRequest
import com.example.foodapp.data.model.Notification
import com.example.foodapp.data.remote.main_api.NotificationApi
import com.example.foodapp.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotificationRepoImpl @Inject constructor(
    private val notificationApi: NotificationApi
) : NotificationRepository {
    override fun getAppNotifications(filter: NotificationFilter): Flow<ApiResponse<List<Notification>>> {
        return apiRequestFlow {
            notificationApi.getAppNotifications(filter.userId, filter.isRead)
        }
    }

    override fun registerToken(request: FcmTokenRequest): Flow<ApiResponse<String>> {
        return apiRequestFlow {
            notificationApi.registerToken(request)
        }
    }

    override fun readNotification(id: Long): Flow<ApiResponse<Unit>> {
        return apiRequestFlow {
            notificationApi.readNotification(id)
        }
    }

    override fun removeToken(request: FcmTokenRequest): Flow<ApiResponse<String>> {
        return apiRequestFlow {
            notificationApi.removeToken(request)

        }
    }

    override fun readAllNotification(userId: String): Flow<ApiResponse<Unit>> {
        return apiRequestFlow {
            notificationApi.readAllNotification(userId)
        }
    }
}