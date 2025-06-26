package com.example.foodapp.domain.use_case.notification

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.filter.NotificationFilter
import com.example.foodapp.data.model.Notification
import com.example.foodapp.domain.repository.NotificationRepository
import com.example.foodapp.domain.use_case.auth.GetUserIdUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val getUserIdUseCase: GetUserIdUseCase,
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke() = flow<ApiResponse<List<Notification>>> {
        try {
            val filter = NotificationFilter(getUserIdUseCase.invoke(), null)
            notificationRepository.getAppNotifications(filter).collect {
                emit(it)
            }
        }catch (e: Exception){
            e.printStackTrace()
            emit(ApiResponse.Failure(e.message?: "Đã có lỗi xảy ra khi lấy thông báo", 999))
        }
    }.flowOn(Dispatchers.IO)

}