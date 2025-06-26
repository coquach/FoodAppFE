package com.example.foodapp.domain.use_case.notification

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.domain.repository.NotificationRepository
import com.example.foodapp.domain.use_case.auth.GetUserIdUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ReadAllNotificationUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val getUserIdUseCase: GetUserIdUseCase
){
    operator fun invoke() = flow<ApiResponse<Unit>> {
        try {
            emit(ApiResponse.Loading)
            val userId = getUserIdUseCase.invoke()
            notificationRepository.readAllNotification(userId).collect {
                emit(it)
            }
        }catch (e: Exception){
            e.printStackTrace()
            emit(ApiResponse.Failure(e.message?: "Đã có lỗi xảy ra đánh dấu đã đọc tất cả thông báo", 999))
        }
    }.flowOn(Dispatchers.IO)
}