package com.example.foodapp.domain.use_case.notification

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.request.FcmTokenRequest
import com.example.foodapp.domain.repository.AccountRepository
import com.example.foodapp.domain.repository.NotificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class RegisterTokenUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
        private val notificationRepository: NotificationRepository
) {
    operator fun invoke(token: String) = flow<ApiResponse<String>> {
        try {
            val role =accountRepository.getUserRole()
            val userType = when(role){
                "staff" -> "SELLER"
                "shipper" -> "SHIPPER"
                else -> "CUSTOMER"
            }
            if(role== "admin") return@flow
            val userId = accountRepository.currentUserId?: throw Exception("Tài khoản chưa đăng nhập")
            val request = FcmTokenRequest(
                token = token,
                userId = userId,
                userType = userType
            )
            notificationRepository.registerToken(request).collect {
                emit(it)
            }
        }catch (e: Exception){
            e.printStackTrace()
            emit(ApiResponse.Failure(e.message?: "Đã có lỗi xảy ra khi đăng ký thông báo", 999))
        }
    }.flowOn(Dispatchers.IO)
}