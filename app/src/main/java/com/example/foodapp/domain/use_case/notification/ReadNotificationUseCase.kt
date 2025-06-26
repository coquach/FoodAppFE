package com.example.foodapp.domain.use_case.notification

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.domain.repository.NotificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ReadNotificationUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(id: Long) = flow<ApiResponse<Unit>> {
        try {
            notificationRepository.readNotification(id).collect {
                emit(it)
            }
        }catch (e: Exception){
            e.printStackTrace()
            emit(ApiResponse.Failure(e.message?: "Đã có lỗi xảy ra đánh dấu đã đọc thông báo", 999))
        }
    }.flowOn(Dispatchers.IO)
}